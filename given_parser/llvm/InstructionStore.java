package llvm;

import ast.*;

public class InstructionStore implements Instruction {

	String value; //target
	String pointer; //source

	public InstructionStore (String target, String source) {
		this.value = source;
		this.pointer = target;
	}

	@Override
	public String toString() {
		return "store i32 " + value + ", i32* " + pointer;
	}
}