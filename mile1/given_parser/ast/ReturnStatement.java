package ast;

public class ReturnStatement
   extends AbstractStatement
{
   private final Expression expression;

   public ReturnStatement(int lineNum, Expression expression)
   {
      super(lineNum);
      this.expression = expression;
   }

   public Expression getExpression() {
   	return expression;
   }

   public void printStatement() {
      System.out.println("ReturnStatement: ");
      this.expression.printExpression();
   }
}
