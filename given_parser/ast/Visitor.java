package ast; 

public interface Visitor {
   public String visit(BoolType bool);
   public String visit(IntType integer);
   public String visit(StructType struct);
   public String visit(BinaryExpression be);
   public String visit(UnaryExpression ue);
}
