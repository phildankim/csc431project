package cfg;

public class Edge {

	private Block from;
	private Block to;

	public Edge(Block from, Block to) {
		this.from = from;
		this.to = to;
	}

	public Block getTo() {
		return this.to;
	}

	public Block getFrom() {
		return this.from;
	}

	public void printEdge() {
		System.out.println("From: " + this.from.getLabel() + "\tTo: " + this.to.getLabel());
	}
}