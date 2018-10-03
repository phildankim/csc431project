package cfg;

import java.util.*;
import ast.*;

public class CFG {

	public ArrayList<Block> blocks = new ArrayList<Block>();
	public ArrayList<Edge> edges = new ArrayList<Edge>();
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
		// Statement statement = f.getBody();
		// if (statement instanceof ConditionalStatement) {

		// 	// Add guard to instructions
		// 	GuardStatement gs = new GuardStatement(((ConditionalStatement)statement).getGuard());
		// 	currBlock.addInstructions(gs);

		// 	// Branch IfThen
		// 	Block ifthen = new Block("Then");
		// 	ifthen.addInstructions(((ConditionalStatement)statement).getThen());

		// 	// Branch IfElse
		// 	Block ifelse = new Block("Else");
		// 	ifelse.addInstructions(((ConditionalStatement)statement).getElse());

		// 	// Add edges
		// 	Edge toThen = new Edge(currBlock, ifthen);
		// 	edges.add(toThen);
		// 	Edge toElse = new Edge(currBlock, ifelse);
		// 	edges.add(toElse);

		// }
		// else if (statement instanceof WhileStatement) {

		// }
		// else {
		// 	currBlock.addInstructions(statement);
		// }
	}

	public void printCFG() {
		for (Block b : blocks) {
			b.printBlock();
		}
	}
}