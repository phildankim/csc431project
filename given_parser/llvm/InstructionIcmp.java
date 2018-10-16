package llvm;

public class InstructionIcmp implements Instruction 
{
	String condition;
	String operand1;
	String operand2;
	Register result;

	public InstructionIcmp (Register result, String condition, String operand1, String operand2) {
		this.result = result;
		this.condition = condition;
		this.operand1 = operand1;
		this.operand2 = operand2;
	}

	@Override
	public String toString() {
		return result.toString() + " = icmp " + condition + " i32 " + operand1 + ", " + operand2;
	}

	// public String convertOpr(String opr) {
	// 	switch(opr) {
	// 		case "TIMES":
	// 			return "*";
	// 		case "DIVIDE":
	// 			return "";
	// 		case "PLUS":
	// 			return "";
	// 		case "MINUS":
	// 			return "";
	// 		case "LT":
	// 			return "";
	// 		case "GT":
	// 			return "";
	// 		case "LE":
	// 			return "";
	// 		case "GE"
	// 			return "";
	// 		case "EQ"
	// 			return "";
	// 		case "NE"
	// 			return "";
	// 		case "AND"
	// 			return "";
	// 		case "OR"
	// 			return "";

	// 	}
	// }

}