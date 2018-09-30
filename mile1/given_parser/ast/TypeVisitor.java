package ast;

class TypeVisitor implements Visitor {

   public TypeVisitor() {
   }

   @Override
   public String visit(BoolType bool) {
      return "BoolType";
   }

   @Override
   public String visit(IntType integer) {
      return "IntType";
   }

   @Override
   public String visit(StructType struct) {
      return "StructType";
   }
}
