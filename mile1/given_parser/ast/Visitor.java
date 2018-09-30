package ast;

public interface Visitor {
   public String visit(BoolType bool);
   public String visit(IntType integer);
   public String visit(StructType struct);
}
