import ast.*;

public class MiniToAstTypeVisitor
   extends MiniBaseVisitor<Type>
{
   @Override
   public Type visitIntType(MiniParser.IntTypeContext ctx)
   {
      return new IntType();
   }

   @Override
   public Type visitBoolType(MiniParser.BoolTypeContext ctx)
   {
      return new BoolType();
   }

   @Override
   public Type visitStructType(MiniParser.StructTypeContext ctx)
   {
      return new StructType(ctx.getStart().getLine(), ctx.ID().getText());
   }

   @Override
   public Type visitReturnTypeReal(MiniParser.ReturnTypeRealContext ctx)
   {
      return visit(ctx.type());
   }

   @Override
   public Type visitReturnTypeVoid(MiniParser.ReturnTypeVoidContext ctx)
   {
      return new VoidType();
   }

   @Override
   protected Type defaultResult()
   {
      return new VoidType();
   }
}
