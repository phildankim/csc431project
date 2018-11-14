package llvm;

public class InstructionPrintLn implements Instruction {
	

	Value register;

	public InstructionPrintLn (Value register) {
		this.register = register;
	}

	@Override
	public String toString() {
		return "call i32 (i8*, ...)* @printf (i8* getelementptr inbounds ([5 x i8]* @.println, i32 0), i32 " + register + ")";
	}
}