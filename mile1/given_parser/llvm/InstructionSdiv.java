package llvm;

public class InstructionSdiv implements Instruction {
	
	String operand1;
	String operand2;
	Register register;

	public InstructionSdiv (Register register, String operand1, String operand2) {
		this.register = register;
		this.operand1 = operand1;
		this.operand2 = operand2;
	}

	@Override
	public String toString() {
		return register.toString() + " = sdiv i32 " + operand1 + ", " + operand2;
	}
}