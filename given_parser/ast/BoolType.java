package ast;

public class BoolType
   implements Type, Visitable
{

	// Added for Milestone 1:
	@Override
	public String toString() {
		return "BoolType";
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
