package ast;

public class IntegerExpression
   extends AbstractExpression
{
   private final String value;

   public IntegerExpression(int lineNum, String value)
   {
      super(lineNum);
      this.value = value;
   }

   public void printExpression() {
      System.out.println("IntegerExpression: " + (this.value));
   }

}
