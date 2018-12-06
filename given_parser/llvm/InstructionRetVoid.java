package llvm;

import java.util.*;

public class InstructionRetVoid implements Instruction {
	
	public InstructionRetVoid () {
	}

	@Override
	public String toString() {
		return "ret void";
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