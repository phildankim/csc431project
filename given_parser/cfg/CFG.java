package cfg;

import java.util.*;
import java.util.regex.Pattern;
import java.io.BufferedWriter;
import java.io.IOException;

import ast.*;
import llvm.*;

public class CFG {

	public ArrayList<Block> blocks = new ArrayList<Block>();
	public ArrayList<Edge> edges = new ArrayList<Edge>();

	// Key: id, Val: type object
	// basically a symbol table
	private static HashMap<String, LLVMObject> locals = new HashMap<String, LLVMObject>();

	public Block entryBlock;
	public Block exitBlock;
	public Block currBlock;

	public int labelCounter;
	public String functionName;

	public Function f;
	public Program p;

	public CFG(Function f, Program p) {
		this.entryBlock = new Block("Entry");
		this.exitBlock = new Block("Exit");
		this.blocks.add(entryBlock);
		this.blocks.add(exitBlock);
		this.currBlock = entryBlock;
		this.labelCounter = 1;
		this.functionName = f.getName();
		this.f = f;
		this.p = p;

		InstructionTranslator.setFunctionReturnInstruction(entryBlock,f.getType());
		InstructionTranslator.setLocalParamInstruction(entryBlock,f.getParams());
		InstructionTranslator.setLocalDeclInstruction(entryBlock, f.getLocals());
	}

	public static void printStructs() {
		System.out.println("--Currently in CFG.Structs");
		for (String key : CFG.locals.keySet()) {
			System.out.println("Key: " + key + "\tValue: " + CFG.getType(key));
		}
	}
	
	public void updateCurr(Block newCurr) {
		this.currBlock = newCurr;
	}

	public int numEdges() {
		return this.edges.size();
	}

	public void clearStructs() {
		CFG.locals.clear();
	}

	public static void addToLocals(String s, LLVMObject t) {
		CFG.locals.put(s, t);
	}

	public static LLVMObject getType(String id) {
		return CFG.locals.get(id);
	}

	public void removeUnnecessaryBranch() {
		for (Block b : this.blocks) {
			if (b.instructions.size() >= 2) {
				if (b.getLastInstruction() instanceof InstructionBr) {
					if (b.instructions.get(b.instructions.size() - 2) instanceof InstructionBr || 
						b.instructions.get(b.instructions.size() - 2) instanceof InstructionBrCond) {
						b.instructions.remove(b.instructions.size()-1);
					}
				}
			}
			if (this.isReturn(b)) {
				if (b.getLastInstruction() instanceof InstructionBr) {
					b.instructions.remove(b.instructions.size() - 1);
				}
			}
			
		}
	}

	public Block createCFG(Statement statement) {

		if (statement instanceof BlockStatement) { 
			
			List<Statement> statements = ((BlockStatement)statement).getStatements();

			Block result = null;

			for (Statement s : statements) {
				result = this.createCFG(s);
			} 


			return result;
		}
		else if (statement instanceof ConditionalStatement) {
			ConditionalStatement cs = (ConditionalStatement)statement;

			// Create Then and Else blocks
			Block ifThen = new Block("Then" + Integer.toString(labelCounter));
			labelCounter += 1;
			blocks.add(ifThen);
			Block ifElse = new Block("Else" + Integer.toString(labelCounter));
			labelCounter += 1;
			blocks.add(ifElse);

			// Create edges
			Edge toThen = new Edge(currBlock, ifThen);
			edges.add(toThen);
			Edge toElse = new Edge(currBlock, ifElse);
			edges.add(toElse); 

			Block join = new Block("Join" + Integer.toString(labelCounter));
			blocks.add(join);

			// Add guard instruction to currBlock
			InstructionTranslator.setGuardInstruction(currBlock, ifThen, ifElse, cs.getGuard(), p, f);

			// Branch IfThen
			this.updateCurr(ifThen);
			Optional<Block> opt = Optional.ofNullable(createCFG(cs.getThen()));
			
			if (opt.isPresent()) {

				if (!(this.isReturn(currBlock))) {
					InstructionBr toJoin = new InstructionBr(join.getLabel());
					currBlock.addInstruction(toJoin);
				}

			}
			else {
				InstructionBr toJoin = new InstructionBr(join.getLabel());
				ifThen.addInstruction(toJoin);

			}

			// Branch IfElse
			this.updateCurr(ifElse);
			opt = Optional.ofNullable(createCFG(cs.getElse()));		
			
			if (opt.isPresent()) {

				if (!(this.isReturn(currBlock))) {
					InstructionBr toJoin = new InstructionBr(join.getLabel());
					currBlock.addInstruction(toJoin);
				}
			}
			else {
				InstructionBr toJoin = new InstructionBr(join.getLabel());
				ifElse.addInstruction(toJoin);
			}

			if (!(this.isReturn(currBlock))) {
				this.updateCurr(join);
			}

			return currBlock;
		}
		else if (statement instanceof WhileStatement) {
			WhileStatement ws = (WhileStatement)statement;

			Block whileGuard = new Block("WhileGuard" + Integer.toString(labelCounter));
			labelCounter += 1;
			Edge toGuard = new Edge(currBlock, whileGuard);
			edges.add(toGuard);
			blocks.add(whileGuard);

			InstructionBr toThisBlock = new InstructionBr(whileGuard.getLabel());
			currBlock.addInstruction(toThisBlock);

			this.updateCurr(whileGuard);

			Block whileBody = new Block("WhileBody" + Integer.toString(labelCounter));
			labelCounter += 1;
			Edge toBody = new Edge(whileGuard, whileBody);
			edges.add(toBody);
			blocks.add(whileBody);

			Block join = new Block("Join" + Integer.toString(labelCounter)); 
			Edge toJoin = new Edge(whileGuard, join);
			blocks.add(join);
			edges.add(toJoin);

			InstructionTranslator.setWhileGuardInstruction(currBlock, join, whileBody, ws.getGuard(), p, f);

			// Branch whilebody
			this.updateCurr(whileBody);
			Block bodyRes = createCFG(ws.getBody());

			InstructionBr instr = new InstructionBr(whileGuard.getLabel());

			if (this.isJoin(bodyRes)) {
				//Branch isntructions here
				bodyRes.addInstruction(instr);

				Edge bodyJoin = new Edge(bodyRes, whileGuard);
				edges.add(bodyJoin);
				InstructionBr joinInstr = new InstructionBr(join.getLabel());
				bodyRes.addInstruction(joinInstr);
			}
			else {
				whileBody.addInstruction(instr);

				Edge whileLoop = new Edge(whileBody, whileGuard);
				edges.add(whileLoop);

			}
			InstructionBr toCurr = new InstructionBr(join.getLabel());
			currBlock.addInstruction(toCurr);
			this.updateCurr(join);
			return currBlock;

		}
		else if (statement instanceof ReturnStatement) {
			
			Block returnBlock = new Block("Return" + Integer.toString(labelCounter));
			blocks.add(returnBlock);
			labelCounter += 1;

			ReturnStatement rs = (ReturnStatement)statement;

			Expression targetExp = rs.getExpression();
			Value resultReg = InstructionTranslator.parseExpression(currBlock,targetExp,p, f);

			Value returnReg = new Register(resultReg.getType(), "_retval_");

			LLVMObject funcReturnType = InstructionTranslator.convertTypeToObject(f.getType());

			Instruction storeToRetVal = new InstructionStore(returnReg, resultReg, funcReturnType);
			currBlock.addInstruction(storeToRetVal);

			InstructionBr branchToReturn = new InstructionBr(returnBlock.getLabel());
			currBlock.addInstruction(branchToReturn);

			//return instruction loads from _retval_ and calls return:
			InstructionTranslator.setReturnInstruction(returnBlock, funcReturnType);

			Edge toReturn = new Edge(currBlock, returnBlock);
			edges.add(toReturn);

			this.updateCurr(returnBlock);

			return null;
		}

		else if (statement instanceof ReturnEmptyStatement) {
			
			Block returnBlock = new Block("Return" + Integer.toString(labelCounter));
			blocks.add(returnBlock);
			labelCounter += 1;

			InstructionBr branchToReturn = new InstructionBr(returnBlock.getLabel());
			currBlock.addInstruction(branchToReturn);

			Instruction instr = new InstructionRetVoid();
			returnBlock.addInstruction(instr);

			Edge toReturn = new Edge(currBlock, returnBlock);
			edges.add(toReturn);

			this.updateCurr(returnBlock);

			return null;
		}
		else {
			// Add instructions to currBlock
			InstructionTranslator.translate(currBlock, statement, p, f);
			return currBlock;
		}
	}

	public void connectBlocks(Block from, Block to) {
		Edge e = new Edge(from, to);
		this.edges.add(e);
	}

	public void addPredecessorsAndSuccessors() {
		clearPredsSuccs();
		for (Block b : this.blocks) {
			Instruction instr = b.getLastInstruction();

			if (instr instanceof InstructionBr) {
				InstructionBr br = (InstructionBr)instr;
				Block destination = getBlock(br.destination);
				b.addSucc(destination);
				destination.addPred(b);
			}

			if (instr instanceof InstructionBrCond) {
				InstructionBrCond brcond = (InstructionBrCond)instr;
				Block thenBlock = getBlock(brcond.labelTrue);
				Block elseBlock = getBlock(brcond.labelFalse);
				b.addSucc(thenBlock);
				thenBlock.addPred(b);
				b.addSucc(elseBlock);
				elseBlock.addPred(b);
			}
		}
		
		// for (Block b : this.blocks) {
		// 	System.out.println("Predecessors for " + b.getLabel());
		// 	for (Block p : b.predecessors) {
		// 		System.out.println("\t" + p.getLabel());
		// 	}
		// 	System.out.println("Successors for " + b.getLabel());
		// 	for (Block s : b.successors) {
		// 		System.out.println("\t" + s.getLabel());
		// 	}
		// 	System.out.println("\n");
		// }
	}

	public void clearPredsSuccs() {
		for (Block b : this.blocks) {
			b.predecessors.clear();
			b.successors.clear();
		}
	}
					
	public Block getBlock(String label) {
		for (Block b : this.blocks) {
			if (b.getLabel().equals(label)) {
				return b;
			}
		}

		return null;
	}

	public void printInstructions(BufferedWriter writer) throws IOException {
		//function header
		String funcHeader = buildFuncHeader(f);

		writer.write(funcHeader + "\n");
		
		writer.write("{" + "\n");

		for (Block b : blocks) {
			b.printInstructions(writer);
		}

		writer.write("}" + "\n");
	}

	public void printToARM(BufferedWriter writer) throws IOException {

		// function header??
		writer.write("\t.align 2\n");
		writer.write("\t.global " + f.getName() + "\n");
		writer.write(f.getName() + ":\n");

		for (Block b : blocks) {
			b.printToARM(writer);
		}

	}

	public void printCFG() {
		
		System.out.println("===== CFG FOR FUNCTION: " + this.functionName + " =====");

		//function header
		String funcHeader = buildFuncHeader(f);

		System.out.println(funcHeader);
		
		System.out.println("{");

		for (Block b : blocks) {
			b.printBlock();
		}
		for (Edge e : edges) {
			e.printEdge();
		}

		System.out.println("}");
	}

	public void connectToExit() {
		String regex = "Return\\d+";

		for (Block b : blocks) {
			if (Pattern.matches(regex, b.getLabel())) {
				Edge toExit = new Edge(b, this.exitBlock);
				edges.add(toExit);
			}
		}
	}

	public void removeEmptyBlocks() {
		for (int i = 0; i < this.blocks.size(); i++) {
			Block b = this.blocks.get(i);
			if (b.instructions.size() == 0) {
				this.blocks.remove(i);
			}
		}
	}

	public void addTerminationInstructionToExit() {
		if (f.getType() instanceof VoidType) {
			InstructionBr toExit = new InstructionBr(this.exitBlock.getLabel());
			currBlock.addInstruction(toExit);

			Instruction instr = new InstructionRetVoid();
			this.exitBlock.addInstruction(instr);
		}
	}


	public boolean isJoin(Block b) {
		String regex = "Join\\d+";
		if (Pattern.matches(regex, b.getLabel())) {
			return true;
		}	
		return false;
	}

	public boolean isReturn(Block b) {
		String regex = "Return\\d+";
		if (Pattern.matches(regex, b.getLabel())) {
			return true;
		}	
		return false;
	}

	public String buildFuncHeader(Function f) {
		String header = "define ";

		if (f.getType() instanceof IntType || f.getType() instanceof BoolType) {
				header += "i32";
		}
		else if (f.getType() instanceof VoidType){
			header += "void";
		}
		else if (f.getType() instanceof StructType) {
			StructType st = (StructType)f.getType();
			header += "%struct." + st.getName() + "*";
		}
		else {
			throw new RuntimeException("building func header but return is not int or bool or void or struct");
		}

		header += " @" + f.getName() + "(";

		List<Declaration> params = f.getParams();

		for (int i = 0; i < params.size(); i++) {
			Declaration currDec = params.get(i);

			if (currDec.getType() instanceof IntType || currDec.getType() instanceof BoolType) {
				header += "i32 %" + currDec.getName();
			}
			else if (currDec.getType() instanceof StructType) {
				StructType st = (StructType)currDec.getType();
				header += "%struct." + st.getName() + "* %" + currDec.getName();
			}


			if (i != (params.size() - 1)) {
				header += ", ";
			}
		}

		header += ")";

		return header;
	}

	public boolean hasBranch(Block b) {
		return b.getLastInstruction() instanceof InstructionBr || 
		b.getLastInstruction() instanceof InstructionBrCond;
	}

	// Milestone 5: CFG Simplification
	public void simplify() {
		
		// Start by removing all empty blocks
		removeUnnecessaryBlocks();

		// Get Preds and Succs
		addPredecessorsAndSuccessors();

		removeUnnecessaryBranch();

		// Combine blocks
		combineBlocks();
		// for (Block b : this.blocks) {
		// 	System.out.println("Predecessors for " + b.getLabel());
		// 	for (Block p : b.predecessors) {
		// 		System.out.println("\t" + p.getLabel());
		// 	}
		// 	System.out.println("Successors for " + b.getLabel());
		// 	for (Block s : b.successors) {
		// 		System.out.println("\t" + s.getLabel());
		// 	}
		// 	System.out.println("\n");
		// }
	}

	public void removeUnnecessaryBlocks() {
		ArrayList<Block> blocksToRemove = new ArrayList<Block>();
		for (Block b : this.blocks) {
			if (hasBranch(b)) {
				Instruction instr = b.getLastInstruction();

				if (instr instanceof InstructionBr) {
					InstructionBr br = (InstructionBr)instr;
					Block destination = getBlock(br.destination);
					br.destination = findFinalDestination(destination, blocksToRemove).getLabel();
				}

				else if (instr instanceof InstructionBrCond) {
					InstructionBrCond brcond = (InstructionBrCond)instr;
					Block thenBlock = getBlock(brcond.labelTrue);
					Block elseBlock = getBlock(brcond.labelFalse);

					String newThenDest = findFinalDestination(thenBlock, blocksToRemove).getLabel();
					String newElseDest = findFinalDestination(elseBlock, blocksToRemove).getLabel();

					brcond.labelTrue = newThenDest;
					brcond.labelFalse = newElseDest;
				}

				else
					continue;
			}
		}
		for (Block b : blocksToRemove) {
			this.blocks.remove(getBlock(b.getLabel()));
		}
	}

	public void combineBlocks() {
		ArrayList<Block> blocksToRemove = new ArrayList<Block>();
		for (Block b : this.blocks) {
			if (b.successors.size() == 1) {
				Block blockBeingAbsorbed = b.successors.get(0);

				if (blockBeingAbsorbed.predecessors.size() == 1) {
					combineInstructions(b, blockBeingAbsorbed);
					blocksToRemove.add(blockBeingAbsorbed);
				}
			}
		}
		for (Block b : blocksToRemove) {
			this.blocks.remove(getBlock(b.getLabel()));
		}
	}

	public void combineInstructions(Block from, Block to) {
		from.instructions.remove(from.getLastInstruction());
		from.instructions.addAll(to.instructions);

		// from.successors.remove(to);
		// from.successors.addAll(to.successors);
		//from.predecessors.remove(to);
		//from.predecessors.addAll(to.predecessors);

		addPredecessorsAndSuccessors();

		// replace all labels going to 'to' to 'from'
		String toLabel = to.getLabel();
		String fromLabel = from.getLabel();

		for (Block b : this.blocks) {
			Instruction instr = b.getLastInstruction();

			if (instr instanceof InstructionBr) {
				InstructionBr br = (InstructionBr)instr;
				if (br.destination.equals(toLabel)) {
					br.destination = fromLabel;
				}
			}

			else if (instr instanceof InstructionBrCond) {
				InstructionBrCond brcond = (InstructionBrCond)instr;

				if (brcond.labelTrue.equals(toLabel)) {
					brcond.labelTrue = fromLabel;
				}

				if (brcond.labelFalse.equals(toLabel)) {
					brcond.labelFalse = fromLabel;
				}
			}

			else
				continue;
		}

	}

	public Block findFinalDestination(Block b, ArrayList<Block> blocksToRemove) {
		if (isEmpty(b)) {
			InstructionBr br = (InstructionBr)b.getLastInstruction();
			blocksToRemove.add(b);
			//System.out.println("removing block: " + b.getLabel());
			return findFinalDestination(getBlock(br.destination), blocksToRemove);
		}
		else 
			return b;
	}

	public boolean isEmpty(Block b) {
		return b.instructions.size() == 1 && b.getLastInstruction() instanceof InstructionBr;
	}

}
