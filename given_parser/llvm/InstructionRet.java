package llvm;

public class InstructionRet implements Instruction {
	
	public Value register;
	public LLVMObject type;

	public InstructionRet (Value register) {
		this.register = register;
		this.type = new IntObject();
	}

	public InstructionRet (Value register, LLVMObject type) {
		this.register = register;
		this.type = type;
	}

	@Override
	public String toString() {
		return "ret "+ type.toString() + " " + register;
	}
}