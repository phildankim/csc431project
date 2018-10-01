package ast;

import java.util.List;

public class Function
{
   private final int lineNum;
   private final String name;
   private final Type retType;
   private final List<Declaration> params;
   private final List<Declaration> locals;
   private final Statement body;

   public Function(int lineNum, String name, List<Declaration> params,
      Type retType, List<Declaration> locals, Statement body)
   {
      this.lineNum = lineNum;
      this.name = name;
      this.params = params;
      this.retType = retType;
      this.locals = locals;
      this.body = body;
   }

   // added for Milestone 1
   public String getName() {
      return name;
   }

   public Type getType () {
      return retType;
   }

   public List<Declaration> getParams() {
      return params;
   }

   public List<Declaration> getLocals() {
      return locals;
   }

   public int getLine() {
      return lineNum;
   }
}
