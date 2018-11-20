package llvm;

import llvm.*;

public class InstructionStub implements Instruction{

	String message;

	public InstructionStub(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "STUB INSTRUCTION FROM " + message ;
	}
}