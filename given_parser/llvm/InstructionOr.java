package llvm;

public class InstructionOr implements Instruction {
	
	String operand1;
	String operand2;
	String register;

	public InstructionOr (String register, String operand1, String operand2) {
		this.register = register;
		this.operand1 = operand1;
		this.operand2 = operand2;
	}

	@Override
	public String toString() {
		return register.toString() + " = or i32 " + operand1 + ", " + operand2;
	}
}