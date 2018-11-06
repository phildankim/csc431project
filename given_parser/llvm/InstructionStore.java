package llvm;

import ast.*;

public class InstructionStore implements Instruction {

	String value; //target
	String pointer; //source
	Type type;

	public InstructionStore (String target, String source) {
		this.value = source;
		this.pointer = target;
		this.type = new IntType();
	}

	public InstructionStore (String target, String source, Type type) {
		this.value = source;
		this.type = type;
		this.pointer = target;

	}

	@Override
	public String toString() {
		return "store " + type + " " + value + ", " + type.toString() +"* " + pointer;
	}
}