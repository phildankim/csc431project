package cfg;

import java.util.*;
import ast.*;
import llvm.*;

public class Block {

	private String label;
	public ArrayList<Edge> edges = new ArrayList<Edge>();
	public ArrayList<Instruction> instructions = new ArrayList<Instruction>();

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

	public void addInstructions(Instruction instr) {
		instructions.add(instr);
	}

	public void printBlock() {
		System.out.println("Block label: " + this.getLabel());
		System.out.println("\tInstructions: ");
		for (Statement s : instructions) {
			s.printStatement();
		}
	}
}