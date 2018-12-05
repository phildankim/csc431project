package llvm;

public class InstructionBr implements Instruction{
	public String destination;

	public InstructionBr (String destination) {
		this.destination = destination;
	}

	@Override
	public String toString() {
		return "br label %" + destination;
	}
}