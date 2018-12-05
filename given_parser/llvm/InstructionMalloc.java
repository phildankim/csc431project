package llvm;

public class InstructionMalloc implements Instruction {
	
	public Value register;
	public int numBytes;

	public InstructionMalloc (Value register, int numBytes) {
		this.register = register;
		this.numBytes = numBytes;
	}

	@Override
	public String toString() {
		return register + " = call i8* @malloc(i32 " + numBytes + ")";
	}
}