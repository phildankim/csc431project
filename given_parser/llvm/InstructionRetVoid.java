package llvm;

public class InstructionRetVoid implements Instruction {
	
	public InstructionRetVoid () {
	}

	@Override
	public String toString() {
		return "ret void";
	}
}