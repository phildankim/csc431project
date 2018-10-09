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
		return result + " = icmp " + condition + " i32 " + operand1 + ", " + operand2;
	}

	// public Operator cond;
	
	// public static BinaryExpression create(int lineNum, String opStr,
 //      Expression left, Expression right)
 //   {
 //      switch (opStr)
 //      {
 //         case TIMES_OPERATOR:
 //            return new BinaryExpression(lineNum, Operator.TIMES, left, right);
 //         case DIVIDE_OPERATOR:
 //            return new BinaryExpression(lineNum, Operator.DIVIDE, left, right);
 //         case PLUS_OPERATOR:
 //            return new BinaryExpression(lineNum, Operator.PLUS, left, right);
 //         case MINUS_OPERATOR:
 //            return new BinaryExpression(lineNum, Operator.MINUS, left, right);
 //         case LT_OPERATOR:
 //            return new BinaryExpression(lineNum, Operator.LT, left, right);
 //         case LE_OPERATOR:
 //            return new BinaryExpression(lineNum, Operator.LE, left, right);
 //         case GT_OPERATOR:
 //            return new BinaryExpression(lineNum, Operator.GT, left, right);
 //         case GE_OPERATOR:
 //            return new BinaryExpression(lineNum, Operator.GE, left, right);
 //         case EQ_OPERATOR:
 //            return new BinaryExpression(lineNum, Operator.EQ, left, right);
 //         case NE_OPERATOR:
 //            return new BinaryExpression(lineNum, Operator.NE, left, right);
 //         case AND_OPERATOR:
 //            return new BinaryExpression(lineNum, Operator.AND, left, right);
 //         case OR_OPERATOR:
 //            return new BinaryExpression(lineNum, Operator.OR, left, right);
 //         default:
 //            throw new IllegalArgumentException();
 //      }
 //   }

 //   private static final String TIMES_OPERATOR = "*";
 //   private static final String DIVIDE_OPERATOR = "/";
 //   private static final String PLUS_OPERATOR = "+";
 //   private static final String MINUS_OPERATOR = "-";
 //   private static final String LT_OPERATOR = "<";
 //   private static final String LE_OPERATOR = "<=";
 //   private static final String GT_OPERATOR = ">";
 //   private static final String GE_OPERATOR = ">=";
 //   private static final String EQ_OPERATOR = "==";
 //   private static final String NE_OPERATOR = "!=";
 //   private static final String AND_OPERATOR = "&&";
 //   private static final String OR_OPERATOR = "||";

 //   public static enum Operator
 //   {
 //      TIMES, DIVIDE, PLUS, MINUS, LT, GT, LE, GE, EQ, NE, AND, OR
 //   }
}