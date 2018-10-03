package cfg;

import java.util.*;

public class CFG {

	public ArrayList<Block> blocks;
	public Block currBlock;

	public CFG() {
		Block entry = new Block("Entry");
		blocks.add(entry);
		currBlock = entry;
	}

	public void createCFG(Function f) {
		// for (Statement s : ((BlockStatement)statement).getStatements()) {
		// 	Type type = checkStatement (s, symbolTable,funcParamsTable, structTable, expectedReturnType);
		// }
		Statement statement = f.getBody();
		if (statement instanceof ConditionalStatement) {
			
		}
		else if (statement instanceof WhileStatement) {

		}
		else {
			currBlock.addInstructions(statement);
		}
	}
}