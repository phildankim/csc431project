package llvm;

import llvm.*;

public class InstructionLoad implements Instruction{
	
	Value result;
	Value pointer;
	LLVMObject type;

	public InstructionLoad (Value result, Value pointer, LLVMObject type) {
		this.result = result;
		this.pointer = pointer;
		this.type = type;
	}

	@Override
	public String toString() {
		return result.toString() + " = load " + type + "* " + pointer; 
	}
}