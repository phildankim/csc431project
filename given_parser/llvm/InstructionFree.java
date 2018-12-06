package llvm;

import java.util.*;

public class InstructionFree implements Instruction {
	
	public Value register;

	public InstructionFree (Value register) {
		this.register = register;
	}

	@Override
	public String toString() {
		return "call void @free(i8* " + register + ")";
	}

	public ArrayList<Value> getRegisters() {
		ArrayList<Value> res = new ArrayList<>();

		res.add(register);
		return res;
	}
}