package ast;

import java.util.List;

public class InvocationExpression
   extends AbstractExpression
{
   private final String name;
   private final List<Expression> arguments;

   public InvocationExpression(int lineNum, String name,
      List<Expression> arguments)
   {
      super(lineNum);
      this.name = name;
      this.arguments = arguments;
   }

   public String getName() {
      return name;
   }

   public List<Expression> getArgs() {
      return arguments;
   }

   public void printExpression() {
      System.out.println("InvocationExpression: " + this.getName());
   }
}
