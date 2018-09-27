import java.util.List;
import java.util.ArrayList;

import ast.*;

public class MiniToAstProgramVisitor
   extends MiniBaseVisitor<Program>
{
   private final MiniToAstTypeDeclarationVisitor typeDeclarationVisitor =
      new MiniToAstTypeDeclarationVisitor();
   private final MiniToAstDeclarationsVisitor declarationsVisitor =
      new MiniToAstDeclarationsVisitor();
   private final MiniToAstFunctionVisitor functionVisitor =
      new MiniToAstFunctionVisitor();

   @Override
   public Program visitProgram(MiniParser.ProgramContext ctx)
   {
      return new Program(
          gatherTypes(ctx.types()),
          gatherDeclarations(ctx.declarations()),
          gatherFunctions(ctx.functions()));
   }

   private List<TypeDeclaration> gatherTypes(MiniParser.TypesContext ctx)
   {
      List<TypeDeclaration> types = new ArrayList<>();

      for (MiniParser.TypeDeclarationContext tctx : ctx.typeDeclaration())
      {
         types.add(typeDeclarationVisitor.visit(tctx));
      }

      return types;
   }

   private List<Declaration> gatherDeclarations(
      MiniParser.DeclarationsContext ctx)
   {
      return declarationsVisitor.visit(ctx);
   }

   private List<Function> gatherFunctions(MiniParser.FunctionsContext ctx)
   {
      List<Function> funcs = new ArrayList<>();

      for (MiniParser.FunctionContext fctx : ctx.function())
      {
         funcs.add(functionVisitor.visit(fctx));
      }

      return funcs;
   }

   @Override
   protected Program defaultResult()
   {
      return new Program(
         new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
   }
}
