package ast;

public abstract class AbstractExpression
   implements Expression
{
   private final int lineNum;

   public AbstractExpression(int lineNum)
   {
      this.lineNum = lineNum;
   }

   public int getLine() {
   	return lineNum;
   }

   public void printExpression() {
   }
}
