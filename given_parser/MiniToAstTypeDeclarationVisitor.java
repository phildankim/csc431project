import java.util.List;
import java.util.ArrayList;

import ast.*;

public class MiniToAstTypeDeclarationVisitor
   extends MiniBaseVisitor<TypeDeclaration>
{
   private final MiniToAstTypeVisitor typeVisitor = new MiniToAstTypeVisitor();

   @Override
   public TypeDeclaration visitTypeDeclaration(
      MiniParser.TypeDeclarationContext ctx)
   {
      return new TypeDeclaration(
         ctx.getStart().getLine(),
         ctx.ID().getText(),
         gatherFieldDeclarations(ctx.nestedDecl()));
   }

   private List<Declaration> gatherFieldDeclarations(
      MiniParser.NestedDeclContext ctx)
   {
      List<Declaration> fields = new ArrayList<>();

      for (MiniParser.DeclContext dctx : ctx.decl())
      {
         fields.add(new Declaration(dctx.getStart().getLine(),
            typeVisitor.visit(dctx.type()),
            dctx.ID().getText()));
      }

      return fields;
   }

   @Override
   protected TypeDeclaration defaultResult()
   {
      return new TypeDeclaration(-1, "invalid", new ArrayList<>());
   }
}
