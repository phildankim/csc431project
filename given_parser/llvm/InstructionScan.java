package llvm;

import java.util.*;

public class InstructionScan implements Instruction {
	

	public Value register;

	public InstructionScan (Value register) {
		this.register = register;
	}

	@Override
	public String toString() {
		return "call i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([4 x i8]* @.read, i32 0, i32 0), i32* " + register + ")";
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