package ast;

public class TrueExpression
   extends AbstractExpression
{
   public TrueExpression(int lineNum)
   {
      super(lineNum);
   }

   public void printExpression() {
      System.out.println("TrueExpression");
   }
}
