package ast;

public class FalseExpression
   extends AbstractExpression
{
   public FalseExpression(int lineNum)
   {
      super(lineNum);
   }

   public void printExpression() {
   		System.out.println("FalseExpression");
   }
}
