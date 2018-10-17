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

	//public Instruction funcDef;

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
		//this.setDeclInstructions();
		//this.setTypeDeclInstructions();
		//this.funcDef = new Instruction()
	}

	public void updateCurr(Block newCurr) {
		this.currBlock = newCurr;
	}

	public int numEdges() {
		return this.edges.size();
	}

	public Block createCFG(Statement statement) {

		if (statement instanceof BlockStatement) { 
			//System.out.println(statement);
			List<Statement> statements = ((BlockStatement)statement).getStatements();

			Block result = null;

			for (Statement s : statements) {
				result = this.createCFG(s);
			} 
			return result;
		}
		else if (statement instanceof ConditionalStatement) {
			//System.out.println(statement);
			ConditionalStatement cs = (ConditionalStatement)statement;

			// Add guard instruction to currBlock
			// ArrayList<Instruction> guard = it.translate(cs.getGuard());
			//System.out.println("guard: " + cs.getGuard());
			// currBlock.addInstruction(guard);

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

			// Branch IfThen
			this.updateCurr(ifThen);
			//System.out.println("[THEN] Currblock: " + currBlock.getLabel());
			Optional<Block> opt = Optional.ofNullable(createCFG(cs.getThen()));
			
			if (opt.isPresent()) {
				Block thenRes = opt.get();
				// Edge toExit = new Edge(thenRes, this.exitBlock);
				// edges.add(toExit);
				// System.out.println("From: " + thenRes.getLabel() + " to exit.");
				//Branh isntructions here
				Edge thenJoin = new Edge(thenRes, join);
				edges.add(thenJoin);
			}

			// Branch IfElse
			this.updateCurr(ifElse);
			//System.out.println("[ELSE] Currblock: " + currBlock.getLabel());
			opt = Optional.ofNullable(createCFG(cs.getElse()));		
			
			if (opt.isPresent()) {
				Block elseRes = opt.get();
				// Edge toExit = new Edge(elseRes, this.exitBlock);
				// edges.add(toExit);
				// System.out.println("From: " + elseRes.getLabel() + " to exit.");
				// return null;
				Edge elseJoin = new Edge(elseRes, join);
				edges.add(elseJoin);
			}

			this.updateCurr(join);
			return currBlock;
		}
		else if (statement instanceof WhileStatement) {
			//System.out.println(statement);

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
			//System.out.println(statement);

			// Add return instruction

			Block returnBlock = new Block("Return" + Integer.toString(labelCounter));
			labelCounter += 1;
			Edge toReturn = new Edge(currBlock, returnBlock);
			edges.add(toReturn);
			blocks.add(returnBlock);

			// return returnBlock;
			return null;
		}
		else {
			//System.out.println(statement);
			
			// Add instructions to currBlock
			System.out.println("SADLKFJA;SLKDF");
			InstructionTranslator.translate(currBlock, statement);
			System.out.println("IN CFG: ");
			currBlock.printBlock();
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
		for (Block b : blocks) {
			b.printBlock();
		}
		for (Edge e : edges) {
			e.printEdge();
		}
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

	// public void topologicalHelper(int i, boolean visited[], Stack stack) {
	// 	visited[i] = true;

	// 	//Iterator<Integer> itr = new Ite

	// } 

	// public void topologicalPrinter() {
	// 	int numEdges = this.numEdges();
	// 	Stack stack = new Stack();
	// 	boolean visited[] = new boolean[numEdges];

	// 	for (int i = 0; i < numEdges; i++) {
	// 		if (!visited[i]) {
	// 			topologicalHelper(i, visited, stack);
	// 		}
	// 	}

	// 	while (!stack.empty()) {
	// 		System.out.println(stack.pop() + " ");
	// 	}
	// }
}