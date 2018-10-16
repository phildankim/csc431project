package cfg;

import java.util.*;
import ast.*;
import llvm.*;

public class Block {

	private String label;
	public ArrayList<Edge> edges = new ArrayList<Edge>();
	public ArrayList<Block> predecessors = new ArrayList<Block>();
	public ArrayList<Block> successors = new ArrayList<Block>();
	public ArrayList<Instruction> instructions = new ArrayList<Instruction>();

	public Block(String label) {
		this.label = label;
	}

	public String getLabel() {
		return this.label;
	}

	public void addPred(Block p) {
		this.predecessors.add(p);
	}
	public void addSucc(Block p) {
		this.successors.add(p);
	}

	public void listEdges() {
		System.out.println("From " + this.label + ":");
		for (Edge e : this.edges) {
			System.out.println("\t" + e.getTo().getLabel());
		}
	}

	public void addInstruction(Instruction instr) {
		instructions.add(instr);
	}

	public void printBlock() {
		System.out.println("Block label: " + this.getLabel());
		System.out.println("\tInstructions: ");
		for (Instruction i : instructions) {
			System.out.println("\t\t" + i.toString());
		}

		System.out.println("\tPredecessors: ");
		for (Block b : predecessors) {
			System.out.println("\t\t" + b.getLabel());
		}
		System.out.println("\tSuccessors: ");
		for (Block b : successors) {
			System.out.println("\t\t" + b.getLabel());
		}
	}
}