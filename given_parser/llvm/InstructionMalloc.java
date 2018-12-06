package llvm;

import java.util.*;

public class InstructionMalloc implements Instruction {
	
	public Value register;
	public int numBytes;

	public InstructionMalloc (Value register, int numBytes) {
		this.register = register;
		this.numBytes = numBytes;
	}

	@Override
	public String toString() {
		return register + " = call i8* @malloc(i32 " + numBytes + ")";
	}
	public ArrayList<Value> getRegisters() {
		ArrayList<Value> res = new ArrayList<>();

		res.add(register);
		return res;
	}

	public ArrayList<Value> getUses() {
		ArrayList<Value> res = new ArrayList<>();
		res.add(register);
		return res;
	}

	public Value getDef() {
		return new NullValue();
	}
}