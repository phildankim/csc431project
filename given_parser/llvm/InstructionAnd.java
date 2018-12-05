package llvm;

public class InstructionAnd implements Instruction {
	
	public Value operand1;
	public Value operand2;
	public Value register;

	public InstructionAnd (Value register, Value operand1, Value operand2) {
		this.register = register;
		this.operand1 = operand1;
		this.operand2 = operand2;
	}

	@Override
	public String toString() {
		return register.toString() + " = and i32 " + operand1 + ", " + operand2;
	}
}