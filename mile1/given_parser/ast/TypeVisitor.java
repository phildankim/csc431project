package ast;

public class TypeVisitor implements Visitor {

   public TypeVisitor() {
   }

   public String visit(BoolType bool) {
      return "BoolType";
   }

   public String visit(IntType integer) {
      return "IntType";
   }

   public String visit(StructType struct) {
      return "StructType";
   }

   public String visit(BinaryExpression be) {
      return "";
   }

   public String visit(UnaryExpression ue) {
      return "";
   }
}
