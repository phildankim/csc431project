package llvm;

import java.util.*;

public class InstructionBr implements Instruction{
	public String destination;

	public InstructionBr (String destination) {
		this.destination = destination;
	}

	@Override
	public String toString() {
		return "br label %" + destination;
	}

	public ArrayList<Value> getRegisters() {
		ArrayList<Value> res = new ArrayList<>();

		return res;
	}

	public ArrayList<Value> getUses() {
		ArrayList<Value> res = new ArrayList<>();

		return res;
	}

	public Value getDef() {
		return new NullValue();
	}
}