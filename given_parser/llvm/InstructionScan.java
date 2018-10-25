package llvm;

public class InstructionScan implements Instruction {
	

	String register;

	public InstructionScan (String register) {
		this.register = register;
	}

	@Override
	public String toString() {
		return "call i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([4 x i8]* @.read, i32 0, i32 0), i32* " + register + ")";
	}
}