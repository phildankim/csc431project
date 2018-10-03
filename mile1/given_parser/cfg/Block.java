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

	public void printBlock() {
		System.out.println("Block label: " + this.getLabel());
		System.out.println("\tInstructions: ");
		for (Statement s : instructions) {
			s.printStatement();
		}
	}
}