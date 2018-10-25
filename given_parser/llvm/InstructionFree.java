package llvm;

public class InstructionFree implements Instruction {
	
	String register;

	public InstructionFree (String register) {
		this.register = register;
	}

	@Override
	public String toString() {
		return "call void i8* @free(i32 " + register + ")";
	}
}