package llvm;

public class InstructionBitcast implements Instruction {
	
	String result;
	String register;
	String structName;

	public InstructionBitcast (String result, String register, String structName) {
		this.result = result;
		this.register = register;
		this.structName = structName;
	}

	@Override
	public String toString() {
		return result + " = bitcast i8* " + register + " to %struct." + structName + "*";
	}
}