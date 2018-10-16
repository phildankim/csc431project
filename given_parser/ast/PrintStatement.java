package ast;

public class PrintStatement
   extends AbstractStatement
{
   private final Expression expression;

   public PrintStatement(int lineNum, Expression expression)
   {
      super(lineNum);
      this.expression = expression;
   }

   public Expression getExpression() {
   	return expression;
   }

   public void printStatement() {
      System.out.println("PrintStatement: ");
      this.getExpression().printExpression();
   }


}
