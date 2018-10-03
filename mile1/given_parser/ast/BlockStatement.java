package ast;

import java.util.List;
import java.util.ArrayList;

public class BlockStatement
   extends AbstractStatement
{
   private final List<Statement> statements;

   public BlockStatement(int lineNum, List<Statement> statements)
   {
      super(lineNum);
      this.statements = statements;
   }

   public static BlockStatement emptyBlock()
   {
      return new BlockStatement(-1, new ArrayList<>());
   }

   public List<Statement> getStatements() {
      return statements;
   }

   public void printStatement() {
      System.out.println("BlockStatement: ");
      for (Statement s : this.getStatements()) {
         s.printStatement();
      }
   }
}
