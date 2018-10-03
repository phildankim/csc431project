package ast;

public class DotExpression
   extends AbstractExpression
{
   private final Expression left;
   private final String id;

   public DotExpression(int lineNum, Expression left, String id)
   {
      super(lineNum);
      this.left = left;
      this.id = id;
   }

   public Expression getLeft() {
   	return left;
   }

   public String getId() {
   	return id;
   }

   public void printExpression() {
      System.out.print("DotExpression: ");
      this.left.printExpression();
      System.out.println(id);
   }
}
