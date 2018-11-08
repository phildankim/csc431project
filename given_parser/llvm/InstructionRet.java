package llvm;

public class InstructionRet implements Instruction {
	
	String register;
	LLVMObject type;

	public InstructionRet (String register) {
		this.register = register;
		this.type = new IntObject();
	}

	public InstructionRet (String register, LLVMObject type) {
		this.register = register;
		this.type = type;
	}

	@Override
	public String toString() {
		return "ret "+ type.toString() + " " + register;
	}
}