package cfg;

public class Edge {

	private Block from;
	private Block to;

	public Edge(Block from, Block to) {
		this.from = from;
		this.to = to;
	}

	// public void setFrom(Block from) {
	// 	this.from = from;
	// }

	// public void setTo(Block to) {
	// 	this.to = to;
	// }

	public Block getTo() {
		return this.to;
	}

	public Block getFrom() {
		return this.from;
	}
}