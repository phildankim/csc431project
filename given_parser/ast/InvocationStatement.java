package ast;

import java.util.List;

public class InvocationStatement
   extends AbstractStatement
{
   private final Expression expression;

   public InvocationStatement(int lineNum, Expression expression)
   {
      super(lineNum);
      this.expression = expression;
   }

   public Expression getExpression() {
   	return expression;
   }

   public void printStatement() {
      System.out.println("InvocationStatement: ");
      this.getExpression().printExpression();
   }
}