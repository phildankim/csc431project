package llvm;

public class InstructionLoad implements Instruction{
	
	String result;
	String pointer;
	LLVMObject type;

	public InstructionLoad (String result, String pointer, LLVMObject type) {
		this.result = result;
		this.pointer = pointer;
		this.type = type;
	}

	@Override
	public String toString() {
		return result.toString() + " = load " + this.type.toString() + "* " + pointer; 
	}
}