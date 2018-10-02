package ast;

public class LvalueDot
   implements Lvalue
{
   private final int lineNum;
   private final Expression left;
   private final String id;

   public LvalueDot(int lineNum, Expression left, String id)
   {
      this.lineNum = lineNum;
      this.left = left;
      this.id = id;
   }

   public String getId() {
      return id;
   }

   public int getLine() {
      return lineNum;
   }

   public Expression getLeft() {
      return left;
   }
}
