package llvm;

public class InstructionFree implements Instruction {
	
	Value register;

	public InstructionFree (Value register) {
		this.register = register;
	}

	@Override
	public String toString() {
		return "call void i8* @free(i32 " + register + ")";
	}
}