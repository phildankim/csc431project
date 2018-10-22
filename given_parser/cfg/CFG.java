package cfg;

import java.util.*;
import java.util.regex.Pattern;
import ast.*;
import llvm.*;

public class CFG {

	public ArrayList<Block> blocks = new ArrayList<Block>();
	public ArrayList<Edge> edges = new ArrayList<Edge>();

	public Block entryBlock;
	public Block exitBlock;
	public Block currBlock;

	public int labelCounter;
	public String functionName;

	public Function f;

	public CFG(Function f) {
		this.entryBlock = new Block("Entry");
		this.exitBlock = new Block("Exit");
		this.blocks.add(entryBlock);
		this.blocks.add(exitBlock);
		this.currBlock = entryBlock;
		this.labelCounter = 1;
		this.functionName = f.getName();
		this.f = f;

		InstructionTranslator.setFunctionReturnInstruction(entryBlock,f.getType());
		InstructionTranslator.setLocalParamInstruction(entryBlock,f.getParams());
		InstructionTranslator.setLocalDeclInstruction(entryBlock, f.getLocals());
	}

	public void updateCurr(Block newCurr) {
		this.currBlock = newCurr;
	}

	public int numEdges() {
		return this.edges.size();
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

			// Add guard instruction to currBlock
			InstructionTranslator.setGuardInstruction(currBlock, ifThen, ifElse, cs.getGuard());

			// Branch IfThen
			this.updateCurr(ifThen);
			Optional<Block> opt = Optional.ofNullable(createCFG(cs.getThen()));
			
			if (opt.isPresent()) {
				Block thenRes = opt.get();
				//Branh isntructions here
				Edge thenJoin = new Edge(thenRes, join);
				edges.add(thenJoin);
			}

			// Branch IfElse
			this.updateCurr(ifElse);
			opt = Optional.ofNullable(createCFG(cs.getElse()));		
			
			if (opt.isPresent()) {
				Block elseRes = opt.get();
				Edge elseJoin = new Edge(elseRes, join);
				edges.add(elseJoin);
			}

			this.updateCurr(join);
			return currBlock;
		}
		else if (statement instanceof WhileStatement) {
			// Add guard instruction

			Block whileGuard = new Block("WhileGuard" + Integer.toString(labelCounter));
			labelCounter += 1;
			Edge toGuard = new Edge(currBlock, whileGuard);
			edges.add(toGuard);
			blocks.add(whileGuard);
			this.updateCurr(whileGuard);

			Block whileBody = new Block("WhileBody" + Integer.toString(labelCounter));
			labelCounter += 1;
			Edge toBody = new Edge(whileGuard, whileBody);
			edges.add(toBody);
			blocks.add(whileBody);
			Edge whileLoop = new Edge(whileBody, whileGuard);
			edges.add(whileLoop);

			// Recurse here
			return currBlock;

		}
		else if (statement instanceof ReturnStatement) {

			Block returnBlock = new Block("Return" + Integer.toString(labelCounter));
			labelCounter += 1;

			//return instruction loads from _retval_ and calls return:

			String returnRegister = Register.getRegName();
			Instruction instr = new InstructionLoad(returnRegister, "%_retval_");
			Instruction ret = new InstructionRet(returnRegister);
			returnBlock.addInstruction(instr);
			returnBlock.addInstruction(ret);

			Edge toReturn = new Edge(currBlock, returnBlock);
			edges.add(toReturn);
			blocks.add(returnBlock);

			return null;
		}
		else {
			// Add instructions to currBlock
			InstructionTranslator.translate(currBlock, statement);
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