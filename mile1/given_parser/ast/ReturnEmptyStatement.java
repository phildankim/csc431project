package ast;

public class ReturnEmptyStatement
   extends AbstractStatement
{
   public ReturnEmptyStatement(int lineNum)
   {
      super(lineNum);
   }

   public void printStatement() {
   	System.out.println("ReturnEmptyStatement");
   }
}
