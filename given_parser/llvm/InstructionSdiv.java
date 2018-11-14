package llvm;

public class InstructionSdiv implements Instruction {
	
	Value operand1;
	Value operand2;
	Value register;

	public InstructionSdiv (Value register, Value operand1, Value operand2) {
		this.register = register;
		this.operand1 = operand1;
		this.operand2 = operand2;
	}

	@Override
	public String toString() {
		return register.toString() + " = sdiv i32 " + operand1 + ", " + operand2;
	}
}