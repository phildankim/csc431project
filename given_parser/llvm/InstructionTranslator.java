package llvm;

import java.util.*;

import ast.*;
import cfg.*;

public class InstructionTranslator {

	public static void translate(Block b, Statement s) {
		
		if (s instanceof BlockStatement) { 
			List<Statement> statements = ((BlockStatement)s).getStatements();

			for (Statement stmt : statements) {
				InstructionTranslator.translate(b, stmt);
			} 
		}
		else if (s instanceof AssignmentStatement) {
			System.out.println(b);
			AssignmentStatement as = (AssignmentStatement)s;
			String target = InstructionTranslator.parseLvalue(as.getTarget());
			String source = InstructionTranslator.parseExpression(as.getSource());
			InstructionStore instr = new InstructionStore(target, source);
			b.addInstruction(instr);
		}

		// if (e instanceof BinaryExpression) {
		// 	String opr = e.getOperator().name();

		// 	switch (opr) {
		// 		case BinaryExpression.Operator.TIMES:
		// 			return InstructionIcmp
		// 		case BinaryExpression.Operator.DIVIDE:
		// 		case BinaryExpression.Operator.PLUS:
		// 		case BinaryExpression.Operator.MINUS:
		// 		case BinaryExpression.Operator.LT:
		// 		case BinaryExpression.Operator.LE
		// 		case BinaryExpression.Operator.GT:
		// 		case BinaryExpression.Operator.GE:
		// 		case BinaryExpression.Operator.EQ:
		// 		case BinaryExpression.Operator.NE:
		// 		case BinaryExpression.Operator.AND:
		// 		case BinaryExpression.Operator.OR:
		// 		default:
		// 			throw new IllegalArgumentException();
		// 	}
		// }
		else {

		}
	}

	public static String parseExpression(Expression e) {
		if (e instanceof IntegerExpression) {
			IntegerExpression ie = (IntegerExpression)e;

			return ie.getValue();
		}
		return "";
	}

	public static String parseLvalue(Lvalue lv) {
		if (lv instanceof LvalueId) {
			LvalueId lvid = (LvalueId)lv;

			return "%" + lvid.getId();
		}
		else { // otherwise, it's an lvaluedot
			LvalueDot lvdot = (LvalueDot)lv;

			String lft = InstructionTranslator.parseExpression(lvdot.getLeft());
			return lft;
		}
	}

	// global/program level type declarations such as structs
	public static void setDeclInstruction(Block b, Declaration decl) {

	}
	public static void setTypeDeclInstruction(Block b, TypeDeclaration type) {
		
	}

}