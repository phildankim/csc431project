package llvm;

public class InstructionIcmp implements Instruction 
{
	String condition;
	String operand1;
	String operand2;
	String result;

	public InstructionIcmp (String result, String condition, String operand1, String operand2) {
		this.result = result;
		this.condition = condition;
		this.operand1 = operand1;
		this.operand2 = operand2;
	}

	@Override
	public String toString() {
		return result + " = icmp " + condition + " i32 " + operand1 + ", " + operand2;
	}

}