package cfg;

import java.util.*;
import ast.*;

public class CFG {

	public ArrayList<Block> blocks = new ArrayList<Block>();
	public ArrayList<Edge> edges = new ArrayList<Edge>();
	public Block currBlock;
	public int labelCounter;

	public CFG() {
		Block entry = new Block("Entry");
		blocks.add(entry);
		currBlock = entry;
		labelCounter = 1;
	}

	public void createCFG(Function f) {
		// for (Statement s : ((BlockStatement)statement).getStatements()) {
		// 	Type type = checkStatement (s, symbolTable,funcParamsTable, structTable, expectedReturnType);
		// }
		Statement statement = f.getBody();
		Block join = Block.createBlock(currBlock, statement, blocks, edges, labelCounter);
	}

	public void printCFG() {
		for (Block b : blocks) {
			b.printBlock();
		}
	}
}