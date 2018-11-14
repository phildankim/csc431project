package llvm;

public class InstructionMul implements Instruction {
	
	Value operand1;
	Value operand2;
	Value register;

	public InstructionMul (Value register, Value operand1, Value operand2) {
		this.register = register;
		this.operand1 = operand1;
		this.operand2 = operand2;
	}

	@Override
	public String toString() {
		return register.toString() + " = mul i32 " + operand1 + ", " + operand2;
	}
}