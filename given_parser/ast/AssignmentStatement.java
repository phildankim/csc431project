package ast;

public class AssignmentStatement
   extends AbstractStatement
{
   private final Lvalue target;
   private final Expression source;

   public AssignmentStatement(int lineNum, Lvalue target, Expression source)
   {
      super(lineNum);
      this.target = target;
      this.source = source;
   }

   public Lvalue getTarget() {
   	return target;
   }

   public Expression getSource() {
   	return source;
   }

   public void printStatement() {
      System.out.println("AssignmentStatement: ");
      this.source.printExpression();
   }

   
}
