package llvm;

public class InstructionMalloc implements Instruction {
	
	String register;
	int numBytes;

	public InstructionMalloc (String register, int numBytes) {
		this.register = register;
		this.numBytes = numBytes;
	}

	@Override
	public String toString() {
		return register + " = call i8* @malloc(i32 " + numBytes + ")";
	}
}