package ast;

public interface Visitable {
	public void accept(Visitor visitor);
}