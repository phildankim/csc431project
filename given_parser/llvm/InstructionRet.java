package llvm;

public class InstructionRet implements Instruction {
	
	String register;

	public InstructionRet (String register) {
		this.register = register;
	}

	@Override
	public String toString() {
		return "ret i32 " + register;
	}
}