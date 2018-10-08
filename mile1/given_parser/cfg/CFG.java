package cfg;

import java.util.*;
import ast.*;

public class CFG {

	public ArrayList<Block> blocks = new ArrayList<Block>();
	public ArrayList<Edge> edges = new ArrayList<Edge>();
	public Block entryBlock;
	public Block exitBlock;
	public Block currBlock;
	public int labelCounter;

	public CFG() {
		this.entryBlock = new Block("Entry");
		this.exitBlock = new Block("Exit");
		this.blocks.add(entryBlock);
		this.blocks.add(exitBlock);
		this.currBlock = entryBlock;
		this.labelCounter = 1;
	}

	public void updateCurr(Block newCurr) {
		this.currBlock = newCurr;
	}

	public Block createCFG(Statement statement) {

		if (statement instanceof BlockStatement) { 
			System.out.println(statement);
			List<Statement> statements = ((BlockStatement)statement).getStatements();

			Block result = null;

			for (Statement s : statements) {
				result = this.createCFG(s);
			} 
			return result;
		}
		else if (statement instanceof ConditionalStatement) {
			System.out.println(statement);
			ConditionalStatement cs = (ConditionalStatement)statement;

			// Add guard instruction to currBlock

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

			// Branch IfThen
			this.updateCurr(ifThen);
			Optional<Block> opt = Optional.ofNullable(createCFG(cs.getThen()));
			if (opt.isPresent()) {
				Edge toExit = new Edge(opt.get(), this.exitBlock);
				edges.add(toExit);
				System.out.println("From: " + opt.get().getLabel() + " to exit.");
			}

			// Branch IfElse
			this.updateCurr(ifElse);
			opt = Optional.ofNullable(createCFG(cs.getElse()));
			if (opt.isPresent()) {
				Edge toExit = new Edge(opt.get(), this.exitBlock);
				edges.add(toExit);
				System.out.println("From: " + opt.get().getLabel() + " to exit.");
			}

			return currBlock;
		}
		else if (statement instanceof WhileStatement) {
			System.out.println(statement);

			// Add guard instruction

			// Block whileGuard = new Block("WhileGuard" + Integer.toString(labelCounter));
			// labelCounter += 1;
			// Edge toGuard = new Edge(currBlock, whileGuard);
			// edges.add(toGuard);
			// blocks.add(whileGuard);

			// Block whileBody = new Block("WhileBody" + Integer.toString(labelCounter));
			// labelCounter += 1;
			// Edge toBody = new Edge(whileGuard, whileBody);
			// edges.add(toBody);
			// blocks.add(whileBody);
			// Edge whileLoop = new Edge(whileBody, whileGuard);
			// edges.add(whileLoop);

			// Recurse here
			return currBlock;

		}
		else if (statement instanceof ReturnStatement) {
			System.out.println(statement);

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
			System.out.println(statement);
			
			// Add instructions to currBlock

			return currBlock;
		}
	}

	public void connectBlocks(Block from, Block to) {
		Edge e = new Edge(from, to);
		this.edges.add(e);
	}

	public void printCFG() {
		System.out.println("Num blocks: " + blocks.size());
		for (Block b : blocks) {
			b.printBlock();
		}
		for (Edge e : edges) {
			e.printEdge();
		}
	}
}