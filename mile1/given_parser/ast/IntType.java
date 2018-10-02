package ast;

public class IntType
   implements Type, Visitable
{

	// Added for Milestone 1:
	@Override
	public String toString() {
		return "IntType";
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
