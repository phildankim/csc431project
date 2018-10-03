package ast;

public class NullExpression
   extends AbstractExpression
{
   public NullExpression(int lineNum)
   {
      super(lineNum);
   }

   public void printExpression() {
      System.out.println("NullExpression");
   }
}
