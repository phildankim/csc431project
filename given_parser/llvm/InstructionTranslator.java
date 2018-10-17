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
	public static InstructionDecl setDeclInstruction(Declaration decl) {
	 	InstructionDecl here = new InstructionDecl(decl);
	 	return here;
	}

	public static InstructionTypeDecl setTypeDeclInstruction(TypeDeclaration type) {
		InstructionTypeDecl typeDecl = new InstructionTypeDecl(type);
		return typeDecl;
	}

	public static void setLocalDeclInstruction(Block b, List<Declaration> locals) {
		for (Declaration d : locals) {
			InstructionAlloca localDecl = new InstructionAlloca(d);
			b.addInstruction(localDecl);
		}
	}

	public static void setLocalParamInstruction(Block b, List<Declaration> params ) {
		for (Declaration param : params) {
			InstructionParamAlloca pAlloc = new InstructionParamAlloca(param);
			b.addInstruction(pAlloc);
		}
	}

	public static void setFunctionReturnInstruction(Block b, Type returnType ) {
		if (returnType instanceof IntType) {
			Declaration d = new Declaration(0, returnType, "_retval_");
			InstructionAlloca returnAlloc = new InstructionAlloca(d);
			b.addInstruction(returnAlloc);
		}
	}

}