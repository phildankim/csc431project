package llvm;

public class InstructionLoad implements Instruction{
	
	Register result;
	String pointer;

	public InstructionLoad (Register result, String pointer) {
		this.result = result;
		this.pointer = pointer;
	}

	@Override
	public String toString() {
		return result.toString() + " = load i32* " + pointer; 
	}
}