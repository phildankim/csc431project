package llvm;

public class InstructionIcmp implements Instruction 
{
	String condition;
	Value operand1;
	Value operand2;
	Value result;

	public InstructionIcmp (Value result, String condition, Value operand1, Value operand2) {
		this.result = result;
		this.condition = condition;
		this.operand1 = operand1;
		this.operand2 = operand2;
	}

	@Override
	public String toString() {
		return result + " = icmp " + condition + " " + operand1.getType() + " " + operand1 + ", " + operand2;
	}

}