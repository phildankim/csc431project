package llvm;

import cfg.*;

public class InstructionStore implements Instruction {

	Value value; //target
	Value pointer; //source
	LLVMObject type;

	public InstructionStore (Value target, Value source) {
		this.value = source;
		this.pointer = target;
		this.type = new IntObject();
	}

	public InstructionStore (Value target, Value source, LLVMObject type) {
		this.value = source;
		this.type = type;
		this.pointer = target;

	}

	@Override
	public String toString() {
		return "store " + type + " " + value + ", " + type +"* " + pointer;
	}
}