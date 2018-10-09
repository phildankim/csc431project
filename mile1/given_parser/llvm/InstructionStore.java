package llvm;

public class InstructionStore implements Instruction {

	String value;
	String pointer;

	public InstructionStore (String value, String pointer) {
		this.value = value;
		this.pointer = pointer;
	}

	@Override
	public String toString() {
		return "store i32 " + value + ", i32* " + pointer;
	}
}