package llvm;

public class InstructionZext implements Instruction {
	
	Value operand1;
	Value operand2;
	Value register;

	public InstructionZext (Value register, Value operand1, Value operand2) {
		this.register = register;
		this.operand1 = operand1;
		this.operand2 = operand2;
	}

	@Override
	public String toString() {
		return register.toString() + " = zext i1 " + operand1 + " to i32";
	}
}