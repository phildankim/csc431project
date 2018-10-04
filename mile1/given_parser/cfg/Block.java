package cfg;

import java.util.*;
import ast.*;

public class Block {

	private String label;
	public ArrayList<Edge> edges = new ArrayList<Edge>();
	public ArrayList<Statement> instructions = new ArrayList<Statement>();

	public Block(String label) {
		this.label = label;
	}

	public String getLabel() {
		return this.label;
	}

	public void listEdges() {
		System.out.println("From " + this.label + ":");
		for (Edge e : this.edges) {
			System.out.println("\t" + e.getTo().getLabel());
		}
	}

	public void addInstructions(Statement statement) {
		instructions.add(statement);
	}

	public static Block createBlock(Block curr, Statement statement, ArrayList<Block> blocks, ArrayList<Edge> edges, int labelCounter) {

		if (statement instanceof BlockStatement) { 
			System.out.println(statement);
			List<Statement> statements = ((BlockStatement)statement).getStatements();

			for (Statement s : statements) {
				Block.createBlock(curr, s, blocks, edges, labelCounter);
			} 
		}
		else if (statement instanceof ConditionalStatement) {
			System.out.println(statement);
			ConditionalStatement cs = (ConditionalStatement)statement;

			// Branch IfThen
			Block ifThen = new Block("Then" + Integer.toString(labelCounter));
			labelCounter += 1;
			Edge toThen = new Edge(curr, ifThen);
			edges.add(toThen);
			blocks.add(ifThen);
			Block thenExit = Block.createBlock(ifThen, cs.getThen(), blocks, edges, labelCounter);


			// Branch IfElse
			Block ifElse = new Block("Else" + Integer.toString(labelCounter));
			System.out.println("here");
			labelCounter += 1;
			Edge toElse = new Edge(curr, ifElse);
			edges.add(toElse);
			blocks.add(ifElse);
			Block elseExit = Block.createBlock(ifElse, cs.getElse(), blocks, edges, labelCounter);

			// Create Join block
			Block join = new Block("Join" + Integer.toString(labelCounter));
			labelCounter += 1;
			Edge thenJoin = new Edge(thenExit, join);
			Edge elseJoin = new Edge(elseExit, join);
			edges.add(thenJoin);
			edges.add(elseJoin);
			blocks.add(join);

			return join;
						
		}
		else if (statement instanceof WhileStatement) {
			System.out.println(statement);
		}
		else if (statement instanceof ReturnStatement) {
			System.out.println(statement);
		}
		else {
			System.out.println(statement);
			
			//curr.addInstructions(statement);
			
		}
		return curr;
	}

	public void printBlock() {
		System.out.println("Block label: " + this.getLabel());
		System.out.println("\tInstructions: ");
		for (Statement s : instructions) {
			s.printStatement();
		}
	}
}