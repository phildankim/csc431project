package ast;

public class StructType
   implements Type
{
   private final int lineNum;
   private final String name;

   public StructType(int lineNum, String name)
   {
      this.lineNum = lineNum;
      this.name = name;
   }

   // added for Milestone 1:

   @Override
   public String toString() {
   	return "StructType";
   }

   public String getName() {
      return name;
   }
}
