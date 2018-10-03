package ast;

public class NewExpression
   extends AbstractExpression
{
   private final String id;

   public NewExpression(int lineNum, String id)
   {
      super(lineNum);
      this.id = id;
   }

   public String getId() {
   	return id;
   }

   public void printExpression() {
      System.out.println("NewExpression: " + this.id);
   }
}
