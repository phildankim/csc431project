package cfg;

import java.util.*;
import ast.*;

public class CFG {

	public ArrayList<Block> blocks = new ArrayList<Block>();
	public ArrayList<Edge> edges = new ArrayList<Edge>();
	public Block entryBlock;
	public int labelCounter;

	public CFG() {
		entryBlock = new Block("Entry");
		blocks.add(entryBlock);
		labelCounter = 1;
	}

	public void createCFG(Function f) {
		// for (Statement s : ((BlockStatement)statement).getStatements()) {
		// 	Type type = checkStatement (s, symbolTable,funcParamsTable, structTable, expectedReturnType);
		// }
		Statement statement = f.getBody();
		Block.createBlock(entryBlock, statement, blocks, edges, labelCounter);
	}

	public void printCFG() {
		System.out.println("Num blocks: " + blocks.size());
		for (Block b : blocks) {
			b.printBlock();
		}
		for (Edge e : edges) {
			e.printEdge();
		}
	}
}