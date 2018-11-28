package llvm;

public class InstructionPrint implements Instruction {
	

	Value register;

	public InstructionPrint (Value register) {
		this.register = register;
	}

	@Override
	public String toString() {
		return "call i32 (i8*, ...)* @printf (i8* getelementptr inbounds ([5 x i8]* @.print, i32 0, i32 0), i32 " + register + ")";
	}
}