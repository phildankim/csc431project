package ast;

public class ConditionalStatement
   extends AbstractStatement
{
   private final Expression guard;
   private final Statement thenBlock;
   private final Statement elseBlock;

   public ConditionalStatement(int lineNum, Expression guard,
      Statement thenBlock, Statement elseBlock)
   {
      super(lineNum);
      this.guard = guard;
      this.thenBlock = thenBlock;
      this.elseBlock = elseBlock;
   }

   public Expression getGuard() {
      return guard;
   }

   public Statement getThen() {
      return thenBlock;
   }

   public Statement getElse() {
      return elseBlock;
   }

   public void printStatement() {
      System.out.println("ConditionalStatement: ");
      this.getGuard().printExpression();
      this.getThen().printStatement();
      this.getElse().printStatement();
   }
}
