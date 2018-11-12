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
				Block thenRes = opt.get();

				//Branch instructions here
				Edge thenJoin = new Edge(thenRes, join);
				InstructionBr toJoin = new InstructionBr(join.getLabel());
				ifThen.addInstruction(toJoin);
			}
			else {
				// Instruction instr = new InstructionRetVoid();
				// ifThen.addInstruction(instr);
				//InstructionBr toJoin = new InstructionBr(join.getLabel());
			}

			// Branch IfElse
			this.updateCurr(ifElse);
			opt = Optional.ofNullable(createCFG(cs.getElse()));		
			
			if (opt.isPresent()) {
				Block elseRes = opt.get();
				Edge elseJoin = new Edge(elseRes, join);
				edges.add(elseJoin);
				InstructionBr toJoin = new InstructionBr(join.getLabel());
				ifElse.addInstruction(toJoin);
			}
			else {

			}

			this.updateCurr(join);
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
			}
			else {
				whileBody.addInstruction(instr);

				Edge whileLoop = new Edge(whileBody, whileGuard);
				edges.add(whileLoop);
			}

			this.updateCurr(join);

			// Recurse here
			return currBlock;

		}
		else if (statement instanceof ReturnStatement) {
			
			Block returnBlock = new Block("Return" + Integer.toString(labelCounter));
			blocks.add(returnBlock);
			labelCounter += 1;

			ReturnStatement rs = (ReturnStatement)statement;

			String toRetVal = Register.getNewRegNum();
			Expression targetExp = rs.getExpression();
			System.out.println("in returnSTatem:" + targetExp);
			String resultReg = InstructionTranslator.parseExpression(currBlock,targetExp,p, f);
			// LLVMObject type = CFG.getObj(resultReg);
			// System.out.println("in return, type: " + resultReg);

			Register r = Register.getReg(resultReg);

			Instruction storeToRetVal = new InstructionStore("%_retval_", resultReg, r.getType());
			currBlock.addInstruction(storeToRetVal);

			InstructionBr branchToReturn = new InstructionBr(returnBlock.getLabel());
			currBlock.addInstruction(branchToReturn);

			//return instruction loads from _retval_ and calls return:
			InstructionTranslator.setReturnInstruction(returnBlock, r.getType());

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
		for (Edge e : this.edges) {
			Block from = e.getFrom();
			Block to = e.getTo();
			from.addSucc(to);
			to.addPred(from);
		}
	}

	public void printInstructions(BufferedWriter writer) throws IOException {
		//System.out.println("===== LLVM FOR FUNCTION: " + this.functionName + " =====");

		//function header
		String funcHeader = buildFuncHeader(f);

		writer.write(funcHeader + "\n");
		
		writer.write("{" + "\n");

		for (Block b : blocks) {
			b.printInstructions(writer);
		}

		writer.write("}" + "\n");
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

	public boolean isJoin(Block b) {
		String regex = "Join\\d+";
		if (Pattern.matches(regex, b.getLabel())) {
			return true;
		}	
		return false;
	}

	public String buildFuncHeader(Function f) {
		String header = "define ";

		if (f.getType() instanceof IntType) {
				header += "i32";
		}
		else {
			header += "void";
		}

		header += " @" + f.getName() + "(";

		List<Declaration> params = f.getParams();

		for (int i = 0; i < params.size(); i++) {
			Declaration currDec = params.get(i);

			header += "i32 %" + currDec.getName();
			if (i != (params.size() - 1)) {
				header += ", ";
			}
		}

		header += ")";

		return header;
	}
}