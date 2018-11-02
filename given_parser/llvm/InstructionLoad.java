package llvm;

import ast.*;

public class InstructionLoad implements Instruction{
	
	String result;
	String pointer;
	Type type;

	public InstructionLoad (String result, String pointer, Type type) {
		this.result = result;
		this.pointer = pointer;
		this.type = type;
	}

	@Override
	public String toString() {
		return result.toString() + " = load " + this.type.toString() + "* " + pointer; 
	}
}