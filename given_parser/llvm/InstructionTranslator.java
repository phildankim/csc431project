package llvm;

import java.util.*;

import ast.*;
import cfg.*;

public class InstructionTranslator {

	public static void translate(Block b, Statement s, Program p) {
		
		if (s instanceof BlockStatement) { 
			List<Statement> statements = ((BlockStatement)s).getStatements();

			for (Statement stmt : statements) {
				InstructionTranslator.translate(b, stmt, p);
			} 
		}
		else if (s instanceof AssignmentStatement) {
			System.out.println(b);
			AssignmentStatement as = (AssignmentStatement)s;
			String target = InstructionTranslator.parseLvalue(b, as.getTarget(), p);
			String source = InstructionTranslator.parseExpression(b, as.getSource(), p);
			InstructionStore instr = new InstructionStore(target, source);
			b.addInstruction(instr);
		}

		else if (s instanceof InvocationStatement) {
			InvocationStatement is = (InvocationStatement)s;
			InstructionTranslator.parseExpression(b, is.getExpression(), p);
		}
		else {

		}
}

	// Returns register or literal
	public static String parseExpression(Block b, Expression e, Program p) {
		if (e instanceof IntegerExpression) {
			IntegerExpression ie = (IntegerExpression)e;

			return ie.getValue();
		}


		else if (e instanceof IdentifierExpression) {
			IdentifierExpression ie = (IdentifierExpression)e;
			String register = Register.getRegName();
			InstructionLoad load = new InstructionLoad (register, "%" + ie.getId());
			b.addInstruction(load);

			return register;
		}

		else if (e instanceof BinaryExpression) {
			BinaryExpression be = (BinaryExpression)e;
			String left = parseExpression(b, be.getLeft(), p);
			String right = parseExpression(b, be.getRight(), p);

			Instruction instr;
			String reg = Register.getRegName();

			switch (be.getOperator()) {
				case PLUS:
					instr = new InstructionAdd(reg, left, right);
					b.addInstruction(instr);
					return reg;
				case MINUS:
					instr = new InstructionSub(reg, left, right);
					b.addInstruction(instr);
					return reg;
				case DIVIDE:
					instr = new InstructionSdiv(reg, left, right);
					b.addInstruction(instr);
					return reg;
				case TIMES:
					instr = new InstructionMul(reg, left, right);
					b.addInstruction(instr);
					return reg;
				case LT:
					instr = new InstructionIcmp(reg, "slt", left, right);
					b.addInstruction(instr);
					return reg;
				case GT:
					instr = new InstructionIcmp(reg, "sgt", left, right);
					b.addInstruction(instr);
					return reg;
				case GE:
					instr = new InstructionIcmp(reg, "sge", left, right);
					b.addInstruction(instr);
					return reg;
				case LE:
					instr = new InstructionIcmp(reg, "sle", left, right);
					b.addInstruction(instr);
					return reg;
				case EQ:
					instr = new InstructionIcmp(reg, "eq", left, right);
					b.addInstruction(instr);
					return reg;
				case NE:
					instr = new InstructionIcmp(reg, "ne", left, right);
					b.addInstruction(instr);
					return reg;
				case AND:
					instr = new InstructionAnd(reg, left, right);
					b.addInstruction(instr);
					return reg;
				case OR:
					instr = new InstructionOr(reg, left, right);
					b.addInstruction(instr);
					return reg;
				default:
					return "";
			}
		}

		else if (e instanceof InvocationExpression) {
			InvocationExpression ie = (InvocationExpression)e;
			ArrayList<String> arguments = new ArrayList<String>();
			for (Expression arg : ie.getArgs()) {
				arguments.add(InstructionTranslator.parseExpression(b,arg, p));
			}

			String retType = "";
			for (Function f : p.getFuncs()) {
				if (ie.getName().equals(f.getName())){
					Type t = f.getType();

					if (t instanceof IntType) {
						retType = "i32";
					}
					else {
						retType = "void";
					}
				}
			}

			if (!retType.equals("void")) {
				String result = Register.getRegName();
				InstructionCall ic = new InstructionCall(result, retType, ie.getName(), arguments);
				b.addInstruction(ic);
			}
			else {
				InstructionCall ic = new InstructionCall("VOID", retType, ie.getName(), arguments);
				b.addInstruction(ic);
			}
		}
		return "";
	}

	public static String parseLvalue(Block b, Lvalue lv, Program p) {
		if (lv instanceof LvalueId) {
			LvalueId lvid = (LvalueId)lv;

			return "%" + lvid.getId();
		}
		else { // otherwise, it's an lvaluedot
			LvalueDot lvdot = (LvalueDot)lv;

			String lft = InstructionTranslator.parseExpression(b, lvdot.getLeft(), p);
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
			Declaration d = new Declaration(0, param.getType(), "_P_" + param.getName());
			InstructionAlloca pAlloc = new InstructionAlloca(d);
			b.addInstruction(pAlloc);

			Instruction instruction = new InstructionStore("%_P_" + param.getName(), "%"+param.getName());
			b.addInstruction(instruction);
		}
	}

	public static void setFunctionReturnInstruction(Block b, Type returnType ) {
		if (returnType instanceof IntType) {
			Declaration d = new Declaration(0, returnType, "_retval_");
			InstructionAlloca returnAlloc = new InstructionAlloca(d);
			b.addInstruction(returnAlloc);
		}
	}

	public static void setGuardInstruction(Block curr, Block ifThen, Block ifElse, Expression e, Program p) {
		String guardReg = InstructionTranslator.parseExpression(curr, e, p);
		InstructionBrCond instr = new InstructionBrCond(guardReg, ifThen.getLabel(), ifElse.getLabel());
		curr.addInstruction(instr);
	}

	public static void setWhileGuardInstruction(Block curr, Block join, Block body, Expression e, Program p) {
		String guardReg = InstructionTranslator.parseExpression(curr, e, p);
		InstructionBrCond instr = new InstructionBrCond(guardReg, body.getLabel(), join.getLabel());
		curr.addInstruction(instr);
	}

}