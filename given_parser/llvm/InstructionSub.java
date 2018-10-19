package llvm;

public class InstructionSub implements Instruction {
	
	String operand1;
	String operand2;
	String register;

	public InstructionSub (String register, String operand1, String operand2) {
		this.register = register;
		this.operand1 = operand1;
		this.operand2 = operand2;
	}

	@Override
	public String toString() {
		return register.toString() + " = sub i32 " + operand1 + ", " + operand2;
	}
}