package llvm;

import cfg.*;

public class InstructionStore implements Instruction {

	String value; //target
	String pointer; //source
	LLVMObject type;

	public InstructionStore (String target, String source) {
		this.value = source;
		this.pointer = target;
		this.type = new IntObject();
	}

	public InstructionStore (String target, String source, LLVMObject type) {
		this.value = source;
		this.type = type;
		this.pointer = target;

	}

	@Override
	public String toString() {
		return "store " + type + " " + value + ", " + type.toString() +"* " + pointer;
	}
}