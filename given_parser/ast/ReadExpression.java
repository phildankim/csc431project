package ast;

public class ReadExpression
   extends AbstractExpression
{
   public ReadExpression(int lineNum)
   {
      super(lineNum);
   }

   public void printExpression() {
      System.out.println("ReadExpression");
   }
}
