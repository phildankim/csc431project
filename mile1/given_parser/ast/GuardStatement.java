package ast;

public class GuardStatement
	//extends AbstractStatement 
{
	private final Expression guard;

	public GuardStatement(Expression guard)
    {
      this.guard = guard;
    }

    public Expression getGuard() {
    	return this.guard;
    }

    public void printStatement() {
    	System.out.println("GuardStatement:");
    	this.getGuard().printExpression();
    }
}