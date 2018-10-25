package llvm;

public class InstructionPrint implements Instruction {
	

	String register;

	public InstructionPrint (String register) {
		this.register = register;
	}

	@Override
	public String toString() {
		return "call i32 (i8*, ...)* @printf (i8* getelementptr inbounds ([5 x i8]* @.println, i32 0), i32 " + register + ")";
	}
}