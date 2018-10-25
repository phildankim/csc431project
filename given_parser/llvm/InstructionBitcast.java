package llvm;

public class InstructionBitcast implements Instruction {
	
	String result;
	String register;
	String structName;
	Boolean isMalloc;

	public InstructionBitcast (String result, String register, String structName, Boolean isMalloc) {
		this.result = result;
		this.register = register;
		this.structName = structName;
		this.isMalloc = isMalloc;
	}

	@Override
	public String toString() {

		if (isMalloc) {
			return result + " = bitcast i8* " + register + " to %struct." + structName + "*";
		}
		else {
			return result + " = bitcast %struct." + structName + "* " + register + " to i8*";
		}
	}
}