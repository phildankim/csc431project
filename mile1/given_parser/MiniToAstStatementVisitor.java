import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;
import java.util.ArrayList;

import ast.*;

public class MiniToAstStatementVisitor
   extends MiniBaseVisitor<Statement>
{
   private final MiniToAstExpressionVisitor expressionVisitor =
      new MiniToAstExpressionVisitor();

   @Override
   public Statement visitNestedBlock(MiniParser.NestedBlockContext ctx)
   {
      return visit(ctx.block());
   }

   @Override
   public Statement visitAssignment(MiniParser.AssignmentContext ctx)
   {
      Expression expression;

      if (ctx.expression() != null)
      {
         expression = expressionVisitor.visit(ctx.expression());
      }
      else
      {
         expression = new ReadExpression(ctx.getStart().getLine());
      }

      return new AssignmentStatement(
         ctx.getStart().getLine(),
         visitLvalue(ctx.lvalue()),
         expression);
   }

   @Override
   public Statement visitPrint(MiniParser.PrintContext ctx)
   {
      return new PrintStatement(
         ctx.getStart().getLine(),
         expressionVisitor.visit(ctx.expression()));
   }

   @Override
   public Statement visitPrintLn(MiniParser.PrintLnContext ctx)
   {
      return new PrintLnStatement(
         ctx.getStart().getLine(),
         expressionVisitor.visit(ctx.expression()));
   }

   @Override
   public Statement visitConditional(MiniParser.ConditionalContext ctx)
   {
      return new ConditionalStatement(
         ctx.getStart().getLine(),
         expressionVisitor.visit(ctx.expression()),
         visit(ctx.thenBlock),
         ctx.elseBlock != null ?
            visit(ctx.elseBlock) : BlockStatement.emptyBlock());
   }

   @Override
   public Statement visitWhile(MiniParser.WhileContext ctx)
   {
      return new WhileStatement(
         ctx.getStart().getLine(),
         expressionVisitor.visit(ctx.expression()),
         visit(ctx.block()));
   }

   @Override
   public Statement visitDelete(MiniParser.DeleteContext ctx)
   {
      return new DeleteStatement(
         ctx.getStart().getLine(),
         expressionVisitor.visit(ctx.expression()));
   }

   @Override
   public Statement visitReturn(MiniParser.ReturnContext ctx)
   {
      if (ctx.expression() != null)
      {
         return new ReturnStatement(ctx.getStart().getLine(),
            expressionVisitor.visit(ctx.expression()));
      }
      else
      {
         return new ReturnEmptyStatement(ctx.getStart().getLine());
      }
   }

   @Override
   public Statement visitInvocation(MiniParser.InvocationContext ctx)
   {
      return new InvocationStatement(
         ctx.getStart().getLine(),
         new InvocationExpression(
            ctx.getStart().getLine(),
            ctx.ID().getText(),
            gatherArguments(ctx.arguments())));
   }

   private List<Expression> gatherArguments(
      MiniParser.ArgumentsContext ctx)
   {
      List<Expression> arguments = new ArrayList<>();

      for (MiniParser.ExpressionContext ectx : ctx.expression())
      {
         arguments.add(expressionVisitor.visit(ectx));
      }

      return arguments;
   }

   @Override
   public Statement visitStatementList(MiniParser.StatementListContext ctx)
   {
      List<Statement> statements = new ArrayList<>();

      for (MiniParser.StatementContext sctx : ctx.statement())
      {
         statements.add(visit(sctx));
      }

      return new BlockStatement(ctx.getStart().getLine(), statements);
   }

   private Lvalue visitLvalue(MiniParser.LvalueContext ctx)
   {
      if (ctx instanceof MiniParser.LvalueIdContext)
      {
         MiniParser.LvalueIdContext lctx = (MiniParser.LvalueIdContext)ctx;
         return new LvalueId(lctx.getStart().getLine(),
            lctx.ID().getText());
      }
      else
      {
         MiniParser.LvalueDotContext lctx = (MiniParser.LvalueDotContext)ctx;
         return new LvalueDot(lctx.getStart().getLine(),
            visitLvalueNested(lctx.lvalue()), lctx.ID().getText());
      }
   }

   private Expression visitLvalueNested(MiniParser.LvalueContext ctx)
   {
      if (ctx instanceof MiniParser.LvalueIdContext)
      {
         MiniParser.LvalueIdContext lctx = (MiniParser.LvalueIdContext)ctx;
         return new IdentifierExpression(lctx.getStart().getLine(),
            lctx.ID().getText());
      }
      else
      {
         MiniParser.LvalueDotContext lctx = (MiniParser.LvalueDotContext)ctx;
         return new DotExpression(lctx.getStart().getLine(),
            visitLvalueNested(lctx.lvalue()), lctx.ID().getText());
      }
   }

   @Override
   public Statement visitBlock(MiniParser.BlockContext ctx)
   {
      return visit(ctx.statementList());
   }

   @Override
   protected Statement defaultResult()
   {
      return BlockStatement.emptyBlock();
   }
}
