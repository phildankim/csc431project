package ast;

public class IdentifierExpression
   extends AbstractExpression
{
   private final String id;

   public IdentifierExpression(int lineNum, String id)
   {
      super(lineNum);
      this.id = id;
   }

   public String getId() {
   	return id;
   }

   public void printExpression() {
      System.out.println("IdentifierExpression: " + this.id);
   }
}
