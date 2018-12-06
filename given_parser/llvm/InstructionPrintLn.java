package llvm;

import java.util.*;

public class InstructionPrintLn implements Instruction {
	

	public Value register;

	public InstructionPrintLn (Value register) {
		this.register = register;
	}

	@Override
	public String toString() {
		return "call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.println, i32 0, i32 0), i32 " + register + ")";
	}

	public ArrayList<Value> getRegisters() {
		ArrayList<Value> res = new ArrayList<>();

		res.add(register);
		return res;
	}
}