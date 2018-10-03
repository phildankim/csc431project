package cfg;

import java.util.*;

public class Block {

	public String label;
	public List<Edge> edges;
	public List<Statement> instructions;

	public Block(String label) {
		this.label = label;
	}

	public String getLabel() {
		return this.label;
	}

	public void listEdges() {
		System.out.println("From " + this.label + ":");
		for (Edge e : this.edges) {
			System.out.println("\t" + e.getTo());
		}
	}

	public void addInstructions(Statement statement) {
		instructions.add(statement);
	}
}