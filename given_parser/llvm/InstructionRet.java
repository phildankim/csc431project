package llvm;

import ast.*;

public class InstructionRet implements Instruction {
	
	String register;
	Type type;

	public InstructionRet (String register) {
		this.register = register;
		this.type = new IntType();
	}

	public InstructionRet (String register, Type type) {
		this.register = register;
		this.type = type;
	}

	@Override
	public String toString() {
		return "ret "+ type.toString() + " " + register;
	}
}