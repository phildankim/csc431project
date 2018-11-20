package cfg;

import java.util.*;
import java.io.BufferedWriter;
import java.io.IOException;

import ast.*;
import llvm.*;

public class Block {

	private String label;
	public ArrayList<Edge> edges = new ArrayList<Edge>();
	public ArrayList<Block> predecessors = new ArrayList<Block>();
	public ArrayList<Block> successors = new ArrayList<Block>();
	public ArrayList<Instruction> instructions = new ArrayList<Instruction>();

	public HashMap<String, Value> currentDef = new HashMap<>();
	private boolean sealed = false;
	private ArrayList<InstructionPhi> phiInstructions = new ArrayList<InstructionPhi>();

	// public ArrayList<Phi> phis = new ArrayList<Phi>();

	public Block(String label) {
		this.label = (label);
	}

	public String getLabel() {
		return this.label;
	}

	public List<Block> getSuccessors() {
		return successors;
	}

	public void seal() {
		sealed = true;
		// get target variables of all incomplete phis in this block
		for (InstructionPhi phi: phiInstructions) {
			// for each variable, fill phi based on predecessors
			addPhiOperands(phi.getId(), phi.getValue().getType(), phi);
		}
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

	public void addPhiInstruction (InstructionPhi phiInstruction) {
		phiInstructions.add(phiInstruction);
	}

	public void writeVariable (String key, Value value) {
		currentDef.put (key,value);
	}

	public Value readVariable (String key, LLVMObject type) {
		Value v = currentDef.get(key);
		if (v == null) {
			v = readVariableRecursive(key,type);
		}

		return v;
	}

	public Value readVariableRecursive(String key, LLVMObject type) {
		if (!sealed) {
			Value val = new Register(type);
			InstructionPhi inst = new InstructionPhi(val,key);
			phiInstructions.add(inst);
			currentDef.put(key,val);
			return val;
		}
		else if (predecessors.size() == 0) {
			if (type instanceof IntObject) {
				return new Immediate("0");
			}
			else if (type instanceof BoolObject) {
				return new Immediate("0");
			}
			else if (type instanceof StructObject) {
				return new NullValue();
			}
			else throw new RuntimeException ("0 predecessors, type object is not struct or bool or int");
		}
		else if (predecessors.size() == 1) {
			Value val = predecessors.get(0).readVariable(key, type);
			currentDef.put(key,val);
			return val;
		}
		else {
			Value val = new Register(type);
			InstructionPhi inst = new InstructionPhi(val, key);
			phiInstructions.add(inst);
			currentDef.put(key,val);
			addPhiOperands(key, type, inst);
			return val;
		}
	}

	public void addPhiOperands (String key, LLVMObject type, InstructionPhi phiInstruction) {
		for (Block predecessor : predecessors) {
			Value val = predecessor.readVariable(key,type);
			phiInstruction.addOperand(predecessor.getLabel(), val);
		}
	}

	public void printInstructions(BufferedWriter writer) throws IOException {
		// if (this.getLabel().equals("Exit")) {
		// 		return;
		// }
		writer.write(this.getLabel() + ":" + "\n");
		for (Instruction i : instructions) {
			writer.write("\t" + i.toString() + "\n");
		}
	}

	public void printSSA(BufferedWriter writer) throws IOException {

		writer.write(this.getLabel() + ":" + "\n");

		for (InstructionPhi i : phiInstructions) {
			writer.write("\t" + i.toString() + "\n");
		}

		for (Instruction i : instructions) {
			writer.write("\t" + i.toString() + "\n");
		}
	}
	public void printBlock() {
		System.out.println("Block label: " + this.getLabel());
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