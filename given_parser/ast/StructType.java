package ast;

public class StructType
   implements Type
{
   private final int lineNum;
   private final String name;
   //private ArrayList<Type> fields = new ArrayList<Type>();

   public StructType(int lineNum, String name)
   {
      this.lineNum = lineNum;
      this.name = name;
   }

   // added for Milestone 1:

   // @Override
   // public String toString() {
   // 	return "StructType";
   // }

   public String toString() {
      return "%struct." + name + "*";
   }

   public String getName() {
      return name;
   }

   // public void addField(Type t) {
   //    this.fields.add(t);
   // }

}
