import javax.json.*;
import org.antlr.v4.runtime.tree.TerminalNode;

public class MiniToJsonVisitor
   extends MiniBaseVisitor<JsonValue>
{
   private final JsonBuilderFactory factory = Json.createBuilderFactory(null);

   @Override
   public JsonValue visitProgram(MiniParser.ProgramContext ctx)
   {
      return factory.createObjectBuilder()
         .add("types", visit(ctx.types()))
         .add("declarations", visit(ctx.declarations()))
         .add("functions", visit(ctx.functions()))
         .build();
   }

   @Override
   public JsonValue visitTypes(MiniParser.TypesContext ctx)
   {
      JsonArrayBuilder abuilder = factory.createArrayBuilder();

      for (MiniParser.TypeDeclarationContext tctx : ctx.typeDeclaration())
      {
         abuilder.add(visit(tctx));
      }

      return abuilder.build();
   }

   @Override
   public JsonValue visitTypeDeclaration(MiniParser.TypeDeclarationContext ctx)
   {
      return factory.createObjectBuilder()
         .add("line", ctx.getStart().getLine())
         .add("id", ctx.ID().getText())
         .add("fields", visit(ctx.nestedDecl()))
         .build();
   }

   @Override
   public JsonValue visitNestedDecl(MiniParser.NestedDeclContext ctx)
   {
      JsonArrayBuilder abuilder = factory.createArrayBuilder();

      for (MiniParser.DeclContext dctx : ctx.decl())
      {
         abuilder.add(visit(dctx));
      }

      return abuilder.build();
   }

   @Override
   public JsonValue visitDecl(MiniParser.DeclContext ctx)
   {
      return factory.createObjectBuilder()
         .add("line", ctx.getStart().getLine())
         .add("type", visit(ctx.type()))
         .add("id", ctx.ID().getText())
         .build();
   }

   @Override
   public JsonValue visitIntType(MiniParser.IntTypeContext ctx)
   {
      JsonArrayBuilder abuilder = factory.createArrayBuilder();

      abuilder.add("int");

      return abuilder.build().getJsonString(0);
   }

   @Override
   public JsonValue visitBoolType(MiniParser.BoolTypeContext ctx)
   {
      JsonArrayBuilder abuilder = factory.createArrayBuilder();

      abuilder.add("bool");

      return abuilder.build().getJsonString(0);
   }

   @Override
   public JsonValue visitStructType(MiniParser.StructTypeContext ctx)
   {
      JsonArrayBuilder abuilder = factory.createArrayBuilder();

      abuilder.add(ctx.ID().getText());

      return abuilder.build().getJsonString(0);
   }

   @Override
   public JsonValue visitDeclarations(MiniParser.DeclarationsContext ctx)
   {
      JsonArrayBuilder abuilder = factory.createArrayBuilder();

      for (MiniParser.DeclarationContext dctx : ctx.declaration())
      {
         addDeclarationsTo(dctx, abuilder);
      }

      return abuilder.build();
   }

   private void addDeclarationsTo(MiniParser.DeclarationContext ctx,
      JsonArrayBuilder abuilder)
   {
      JsonValue type = visit(ctx.type());

      for (TerminalNode node : ctx.ID())
      {
         abuilder.add(factory.createObjectBuilder()
            .add("line", node.getSymbol().getLine())
            .add("type", type)
            .add("id", node.getText())
         );
      }
   }

   @Override
   public JsonValue visitDeclaration(MiniParser.DeclarationContext ctx)
   {
      JsonArrayBuilder abuilder = factory.createArrayBuilder();

      addDeclarationsTo(ctx, abuilder);

      return abuilder.build();
   }

   @Override
   public JsonValue visitFunctions(MiniParser.FunctionsContext ctx)
   {
      JsonArrayBuilder abuilder = factory.createArrayBuilder();

      for (MiniParser.FunctionContext fctx : ctx.function())
      {
         abuilder.add(visit(fctx));
      }

      return abuilder.build();
   }

   @Override
   public JsonValue visitFunction(MiniParser.FunctionContext ctx)
   {
      return factory.createObjectBuilder()
         .add("line", ctx.getStart().getLine())
         .add("id", ctx.ID().getText())
         .add("parameters", visit(ctx.parameters()))
         .add("return_type", visit(ctx.returnType()))
         .add("declarations", visit(ctx.declarations()))
         .add("body", visit(ctx.statementList()))
         .build();
   }

   @Override
   public JsonValue visitParameters(MiniParser.ParametersContext ctx)
   {
      JsonArrayBuilder abuilder = factory.createArrayBuilder();

      for (MiniParser.DeclContext dctx : ctx.decl())
      {
         abuilder.add(visit(dctx));
      }

      return abuilder.build();
   }

   @Override
   public JsonValue visitReturnTypeReal(MiniParser.ReturnTypeRealContext ctx)
   {
      return visit(ctx.type());
   }

   @Override
   public JsonValue visitReturnTypeVoid(MiniParser.ReturnTypeVoidContext ctx)
   {
      JsonArrayBuilder abuilder = factory.createArrayBuilder();

      abuilder.add("void");

      return abuilder.build().getJsonString(0);
   }

   @Override
   public JsonValue visitNestedBlock(MiniParser.NestedBlockContext ctx)
   {
      return visit(ctx.block());
   }

   @Override
   public JsonValue visitAssignment(MiniParser.AssignmentContext ctx)
   {
      JsonValue expression;

      if (ctx.expression() != null)
      {
         expression = visit(ctx.expression());
      }
      else
      {
         expression = factory.createObjectBuilder()
            .add("line", ctx.getStart().getLine())
            .add("exp", "read")
            .build();
      }

      return factory.createObjectBuilder()
         .add("line", ctx.getStart().getLine())
         .add("stmt", "assign")
         .add("source", expression)
         .add("target", visit(ctx.lvalue()))
         .build();
   }

   @Override
   public JsonValue visitPrint(MiniParser.PrintContext ctx)
   {
      return factory.createObjectBuilder()
         .add("line", ctx.getStart().getLine())
         .add("stmt", "print")
         .add("exp", visit(ctx.expression()))
         .add("endl", false)
         .build();
   }

   @Override
   public JsonValue visitPrintLn(MiniParser.PrintLnContext ctx)
   {
      return factory.createObjectBuilder()
         .add("line", ctx.getStart().getLine())
         .add("stmt", "print")
         .add("exp", visit(ctx.expression()))
         .add("endl", true)
         .build();
   }

   @Override
   public JsonValue visitConditional(MiniParser.ConditionalContext ctx)
   {
      JsonObjectBuilder obuilder = factory.createObjectBuilder()
         .add("line", ctx.getStart().getLine())
         .add("stmt", "if")
         .add("guard", visit(ctx.expression()))
         .add("then", visit(ctx.thenBlock));

      if (ctx.elseBlock != null)
      {
         obuilder.add("else", visit(ctx.elseBlock));
      }

      return obuilder.build();
   }

   @Override
   public JsonValue visitWhile(MiniParser.WhileContext ctx)
   {
      return factory.createObjectBuilder()
         .add("line", ctx.getStart().getLine())
         .add("stmt", "while")
         .add("guard", visit(ctx.expression()))
         .add("body", visit(ctx.block()))
         .build();
   }

   @Override
   public JsonValue visitDelete(MiniParser.DeleteContext ctx)
   {
      return factory.createObjectBuilder()
         .add("line", ctx.getStart().getLine())
         .add("stmt", "delete")
         .add("exp", visit(ctx.expression()))
         .build();
   }

   @Override
   public JsonValue visitReturn(MiniParser.ReturnContext ctx)
   {
      JsonObjectBuilder obuilder = factory.createObjectBuilder()
         .add("line", ctx.getStart().getLine())
         .add("stmt", "return");

      if (ctx.expression() != null)
      {
         obuilder.add("exp", visit(ctx.expression()));
      }

      return obuilder.build();
   }

   @Override
   public JsonValue visitInvocation(MiniParser.InvocationContext ctx)
   {
      return factory.createObjectBuilder()
         .add("line", ctx.getStart().getLine())
         .add("stmt", "invocation")
         .add("id", ctx.ID().getText())
         .add("args", visit(ctx.arguments()))
         .build();
   }

   @Override
   public JsonValue visitStatementList(MiniParser.StatementListContext ctx)
   {
      JsonArrayBuilder abuilder = factory.createArrayBuilder();

      for (MiniParser.StatementContext sctx : ctx.statement())
      {
         abuilder.add(visit(sctx));
      }

      return abuilder.build();
   }

   @Override
   public JsonValue visitLvalueId(MiniParser.LvalueIdContext ctx)
   {
      return factory.createObjectBuilder()
         .add("line", ctx.getStart().getLine())
         .add("id", ctx.ID().getText())
         .build();
   }

   @Override
   public JsonValue visitLvalueDot(MiniParser.LvalueDotContext ctx)
   {
      return factory.createObjectBuilder()
         .add("line", ctx.getStart().getLine())
         .add("left", visit(ctx.lvalue()))
         .add("id", ctx.ID().getText())
         .build();
   }

   @Override
   public JsonValue visitIntegerExpr(MiniParser.IntegerExprContext ctx)
   {
      return factory.createObjectBuilder()
         .add("line", ctx.getStart().getLine())
         .add("exp", "num")
         .add("value", ctx.INTEGER().getText())
         .build();
   }

   @Override
   public JsonValue visitTrueExpr(MiniParser.TrueExprContext ctx)
   {
      return factory.createObjectBuilder()
         .add("line", ctx.getStart().getLine())
         .add("exp", "true")
         .build();
   }

   @Override
   public JsonValue visitIdentifierExpr(MiniParser.IdentifierExprContext ctx)
   {
      return factory.createObjectBuilder()
         .add("line", ctx.getStart().getLine())
         .add("exp", "id")
         .add("id", ctx.ID().getText())
         .build();
   }

   @Override
   public JsonValue visitBinaryExpr(MiniParser.BinaryExprContext ctx)
   {
      return factory.createObjectBuilder()
         .add("line", ctx.op.getLine())
         .add("exp", "binary")
         .add("operator", ctx.op.getText())
         .add("lft", visit(ctx.lft))
         .add("rht", visit(ctx.rht))
         .build();
   }

   @Override
   public JsonValue visitNewExpr(MiniParser.NewExprContext ctx)
   {
      return factory.createObjectBuilder()
         .add("line", ctx.getStart().getLine())
         .add("exp", "new")
         .add("id", ctx.ID().getText())
         .build();
   }

   @Override
   public JsonValue visitDotExpr(MiniParser.DotExprContext ctx)
   {
      return factory.createObjectBuilder()
         .add("line", ctx.getStart().getLine())
         .add("exp", "dot")
         .add("left", visit(ctx.expression()))
         .add("id", ctx.ID().getText())
         .build();
   }

   private static boolean doubleNegatingUnary(String op,
      MiniParser.ExpressionContext opnd)
   {
      return (op.equals("!") || op.equals("-")) &&
         opnd instanceof MiniParser.UnaryExprContext &&
         ((MiniParser.UnaryExprContext)opnd).op.getText().equals(op);
   }

   @Override
   public JsonValue visitUnaryExpr(MiniParser.UnaryExprContext ctx)
   {
      String op = ctx.op.getText();
      MiniParser.ExpressionContext opnd = ctx.expression();

      if (doubleNegatingUnary(op, opnd))
      {
         return visit(((MiniParser.UnaryExprContext)opnd).expression());
      }
      else
      {
         return factory.createObjectBuilder()
            .add("line", ctx.getStart().getLine())
            .add("exp", "unary")
            .add("operator", op)
            .add("operand", visit(opnd))
            .build();
      }
   }

   @Override
   public JsonValue visitInvocationExpr(MiniParser.InvocationExprContext ctx)
   {
      return factory.createObjectBuilder()
         .add("line", ctx.getStart().getLine())
         .add("exp", "invocation")
         .add("id", ctx.ID().getText())
         .add("args", visit(ctx.arguments()))
         .build();
   }

   @Override
   public JsonValue visitFalseExpr(MiniParser.FalseExprContext ctx)
   {
      return factory.createObjectBuilder()
         .add("line", ctx.getStart().getLine())
         .add("exp", "false")
         .build();
   }

   @Override
   public JsonValue visitNullExpr(MiniParser.NullExprContext ctx)
   {
      return factory.createObjectBuilder()
         .add("line", ctx.getStart().getLine())
         .add("exp", "null")
         .build();
   }

   @Override
   public JsonValue visitArguments(MiniParser.ArgumentsContext ctx)
   {
      JsonArrayBuilder abuilder = factory.createArrayBuilder();

      for (MiniParser.ExpressionContext ectx : ctx.expression())
      {
         abuilder.add(visit(ectx));
      }

      return abuilder.build();
   }

   @Override
   public JsonValue visitBlock(MiniParser.BlockContext ctx)
   {
      return factory.createObjectBuilder()
         .add("stmt", "block")
         .add("list", visit(ctx.statementList()))
         .build();
   }

   @Override
   public JsonValue visitNestedExpr(MiniParser.NestedExprContext ctx)
   {
      return visit(ctx.expression());
   }
}
