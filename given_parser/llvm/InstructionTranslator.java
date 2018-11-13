package llvm;

import java.util.*;

import ast.*;
import cfg.*;

public class InstructionTranslator {

	public static void translate(Block b, Statement s, Program p, Function f) {
		
		if (s instanceof BlockStatement) { 
			List<Statement> statements = ((BlockStatement)s).getStatements();

			for (Statement stmt : statements) {
				InstructionTranslator.translate(b, stmt, p, f);
			} 
		}
		else if (s instanceof AssignmentStatement) {
			AssignmentStatement as = (AssignmentStatement)s;
			
			String target = InstructionTranslator.parseLvalue(b, as.getTarget(), p, f);
			Register targetReg = Register.getReg(target);
			targetReg.printRegister();

			if (as.getSource() instanceof ReadExpression) {
				InstructionScan ir = new InstructionScan(target);
				b.addInstruction(ir);
			}
			else {
				String source = InstructionTranslator.parseExpression(b, as.getSource(), p, f);
				Register sourceReg = Register.getReg(source);
				LLVMObject sourceType;

				if (sourceReg != null) {
					sourceType = sourceReg.getType();
					if (sourceType instanceof IntObject) {
						IntObject obj = (IntObject)sourceType;
						source = obj.getValue();
					}
				}
				else {
					sourceType = new IntObject();
				}
				InstructionStore instr = new InstructionStore(target, source, sourceType);
				b.addInstruction(instr);
			}
		}

		else if (s instanceof InvocationStatement) {
			InvocationStatement is = (InvocationStatement)s;
			InstructionTranslator.parseExpression(b, is.getExpression(), p, f);
		}

		else if (s instanceof PrintStatement) {
			PrintStatement ps = (PrintStatement)s;
			String psResult = InstructionTranslator.parseExpression(b, ps.getExpression(), p, f);
			InstructionPrint ip = new InstructionPrint(psResult);
			b.addInstruction(ip);
		}

		else if (s instanceof PrintLnStatement) {
			PrintLnStatement ps = (PrintLnStatement)s;
			String psResult = InstructionTranslator.parseExpression(b, ps.getExpression(), p, f);
			InstructionPrintLn ip = new InstructionPrintLn(psResult);
			b.addInstruction(ip);
		}

		else if (s instanceof DeleteStatement) {
			DeleteStatement ds = (DeleteStatement)s;

			// find struct name for expression:
			String structName = "IDIDNTFINDIT";

			if (ds.getExpression() instanceof IdentifierExpression) {
				IdentifierExpression ie = (IdentifierExpression)ds.getExpression();
				String id = ie.getId();

				LLVMObject so = CFG.getType(id);
				if (so instanceof StructObject) {
					structName = ((StructObject)so).getName();
				}
			}


			String regForLoad = InstructionTranslator.parseExpression(b, ds.getExpression(),p, f);
			String regForBitcast = Register.getNewRegNum();

			Instruction bc = new InstructionBitcast(regForBitcast,regForLoad,structName, false);
			b.addInstruction(bc);

			Instruction callvoid = new InstructionFree(regForBitcast);
			b.addInstruction(callvoid);

			// %u82 = load %struct.foo** %math1
			// %u83 = bitcast %struct.foo* %u82 to i8*
			// call void @free(i8* %u83)
		}

		else {
			System.out.println ("InstructionTranslator.Java....YOURE NOT SUPPOSED TO END UP HERE!");
		}
}

	// Returns register or literal
	public static String parseExpression(Block b, Expression e, Program p, Function f) {

		if (e instanceof IntegerExpression) {
			IntegerExpression ie = (IntegerExpression)e;
			IntObject i = new IntObject();
			i.setValue(ie.getValue());

			/*

			Register reg = new Register(Register.getNewRegNum(), i);
			Register.addToRegisters(reg.getRegNum(), reg);

			InstructionStore is = new InstructionStore(reg.getRegNum(), ie.getValue());
			b.addInstruction(is);

			return reg.getRegNum();
			*/

			return ie.getValue();
		}

		else if (e instanceof IdentifierExpression) {
			IdentifierExpression ie = (IdentifierExpression)e;

			String id = ie.getId();
			LLVMObject type = CFG.getType(id);

			String regNum = Register.getNewRegNum();
			Register reg = new Register(regNum, type);
			Register.addToRegisters(regNum, reg);

			InstructionLoad load;

			for (Declaration param : f.getParams()) {
				if (id.equals(param.getName())) {
					load = new InstructionLoad(regNum, "%_P_" + id, type);
					b.addInstruction(load);
					return regNum;
				}
 			}

			load = new InstructionLoad(regNum, "%" + id, type);
			b.addInstruction(load);

			return regNum;
		}

		else if (e instanceof BinaryExpression) {
			BinaryExpression be = (BinaryExpression)e;
			String left = parseExpression(b, be.getLeft(), p, f);
			String right = parseExpression(b, be.getRight(), p, f);

			Instruction instr;
			String reg = Register.getNewRegNum();
			LLVMObject type;
			Register r;

			switch (be.getOperator()) {
				case PLUS:
					type = new IntObject();
					r = new Register(reg, type);
					Register.addToRegisters(reg, r);
					instr = new InstructionAdd(reg, left, right);
					b.addInstruction(instr);
					return reg;
				case MINUS:
					type = new IntObject();
					r = new Register(reg, type);
					Register.addToRegisters(reg, r);
					instr = new InstructionSub(reg, left, right);
					b.addInstruction(instr);
					return reg;
				case DIVIDE:
					type = new IntObject();
					r = new Register(reg, type);
					Register.addToRegisters(reg, r);
					instr = new InstructionSdiv(reg, left, right);
					b.addInstruction(instr);
					return reg;
				case TIMES:
					type = new IntObject();
					r = new Register(reg, type);
					Register.addToRegisters(reg, r);
					instr = new InstructionMul(reg, left, right);
					b.addInstruction(instr);
					return reg;
				case LT:
					type = new IntObject();
					r = new Register(reg, type);
					Register.addToRegisters(reg, r);
					instr = new InstructionIcmp(reg, "slt", left, right);
					b.addInstruction(instr);
					return reg;
				case GT:
					type = new IntObject();
					r = new Register(reg, type);
					Register.addToRegisters(reg, r);
					instr = new InstructionIcmp(reg, "sgt", left, right);
					b.addInstruction(instr);
					return reg;
				case GE:
					type = new IntObject();
					r = new Register(reg, type);
					Register.addToRegisters(reg, r);
					instr = new InstructionIcmp(reg, "sge", left, right);
					b.addInstruction(instr);
					return reg;
				case LE:
					type = new IntObject();
					r = new Register(reg, type);
					Register.addToRegisters(reg, r);
					instr = new InstructionIcmp(reg, "sle", left, right);
					b.addInstruction(instr);
					return reg;
				case EQ:
					type = new BoolObject();
					r = new Register(reg, type);
					Register.addToRegisters(reg, r);
					instr = new InstructionIcmp(reg, "eq", left, right);
					b.addInstruction(instr);
					return reg;
				case NE:
					type = new BoolObject();
					r = new Register(reg, type);
					Register.addToRegisters(reg, r);
					instr = new InstructionIcmp(reg, "ne", left, right);
					b.addInstruction(instr);
					return reg;
				case AND:
					type = new BoolObject();
					r = new Register(reg, type);
					Register.addToRegisters(reg, r);
					instr = new InstructionAnd(reg, left, right);
					b.addInstruction(instr);
					return reg;
				case OR:
					type = new BoolObject();
					r = new Register(reg, type);
					Register.addToRegisters(reg, r);
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
				arguments.add(InstructionTranslator.parseExpression(b,arg, p, f));
			}

			LLVMObject retType = new VoidObject();
			for (Function func : p.getFuncs()) {
				if (ie.getName().equals(func.getName())){
					Type t = func.getType();

					// expand for all types
					if (t instanceof IntType) {
						retType = new IntObject();
					}
					else if (t instanceof BoolType) {
						retType = new BoolObject();
					}
					else {
						retType = new VoidObject();
					}
				}
			}

			// if not void, then store in register and return reg
			if (!(retType instanceof VoidObject)) {
				String result = Register.getNewRegNum();
				Register r = new Register(result, retType);
				Register.addToRegisters(result, r);
				InstructionCall ic = new InstructionCall(result, retType, ie.getName(), arguments);
				b.addInstruction(ic);
				return result;
			}
			else {
				InstructionCall ic = new InstructionCall("VOID", retType, ie.getName(), arguments);
				b.addInstruction(ic);
			}
		}

		else if (e instanceof NewExpression) {

			NewExpression ne = (NewExpression)e;

			String regForMalloc = Register.getNewRegNum();
			String regForBitcast = Register.getNewRegNum();
			String structName = ne.getId();

			// count the number of fields inside the struct:
			int numFields = 0;
			int bytesToAllocate = 0;
			for (TypeDeclaration td : p.getTypes()) {
				if (structName.equals(td.getName())) {
					numFields = td.getFields().size();
				}
			}

			bytesToAllocate = numFields * 8;
			Instruction ma = new InstructionMalloc(regForMalloc,bytesToAllocate);
			b.addInstruction(ma);

			Instruction bc = new InstructionBitcast(regForBitcast,regForMalloc,structName, true);
			b.addInstruction(bc);

			Register reg = new Register(regForBitcast, new StructObject(structName));
			Register.addToRegisters(reg.getRegNum(), reg);
			return regForBitcast;

		}

		else if (e instanceof DotExpression) {
			DotExpression de = (DotExpression)e;
			String dotId = de.getId();

			String leftRegNum = InstructionTranslator.parseExpression(b, de.getLeft(), p, f);
			Register leftReg = Register.getReg(leftRegNum);
			StructObject leftType = (StructObject)leftReg.getType();

			LLVMObject idStruct = LLVM.getStructField(leftType.getName(), dotId);

			Register idRes = new Register(Register.getNewRegNum(), idStruct);
			Register.addToRegisters(idRes.getRegNum(), idRes);

			int index = LLVM.getFieldIndex(leftType.getName(), de.getId());

			InstructionGetElementPtr gep = new InstructionGetElementPtr(idRes.getRegNum(), leftType.toString(), leftReg.getRegNum(), Integer.toString(index));
			b.addInstruction(gep);
			return idRes.getRegNum();
		}

		return "";
	}

	public static LLVMObject convertDeclarationToObject(Declaration d) {
		Type t = d.getType();

		if (t instanceof IntType) {
			return new IntObject(d.getName());
		}
		else if (t instanceof BoolType) {
			return new BoolObject(d.getName());
		}
		else if (t instanceof StructType) {
			StructType st = (StructType)t;
			String structName = st.getName();
			return new StructObject(structName, d.getName());
		}
		else {
			return new VoidObject();
		}

	}

	public static LLVMObject convertTypeToObject(Type t) {
		if (t instanceof IntType) {
			return new IntObject();
		}
		else if (t instanceof BoolType) {
			return new BoolObject();
		}
		else if (t instanceof StructType) {
			StructType st = (StructType)t;
			String structName = st.getName();
			return new StructObject(structName);
		}
		else {
			return new VoidObject();
		}

	}

	public static String parseLvalue(Block b, Lvalue lv, Program p, Function f) {
		if (lv instanceof LvalueId) {
			LvalueId lvid = (LvalueId)lv;
			String id = lvid.getId();
			LLVMObject type = CFG.getType(id);

			String regNum = Register.getNewRegNum();
			Register reg = new Register(regNum, type);
			Register.addToRegisters(regNum, reg);

			InstructionLoad load = new InstructionLoad(regNum, "%" + id, type);
			b.addInstruction(load);
			return regNum;
		}
		else { // otherwise, it's an lvaluedot
			LvalueDot lvdot = (LvalueDot)lv;	
			String lvId = lvdot.getId();
			String lft = InstructionTranslator.parseExpression(b, lvdot.getLeft(), p, f);

			Register regLeft = Register.getReg(lft);
			StructObject dotType = (StructObject)regLeft.getType();
			Register dotRes = new Register(Register.getNewRegNum(), dotType);
			Register.addToRegisters(dotRes.getRegNum(), dotRes);

			LLVMObject idObj = LLVM.getStructField(dotType.getName(), lvId);
			Register idRes = new Register(Register.getNewRegNum(), idObj);
			Register.addToRegisters(idRes.getRegNum(), idRes);

			int index = LLVM.getFieldIndex(dotType.getName(), lvId);

			// instruction to load dottype into new register
			InstructionLoad load = new InstructionLoad(dotRes.getRegNum(), lft, dotType);
			b.addInstruction(load);

			InstructionGetElementPtr gep = new InstructionGetElementPtr(idRes.getRegNum(), dotType.toString(), regLeft.getRegNum(), Integer.toString(index));
			b.addInstruction(gep);
			return idRes.getRegNum();
		}
	}

	// global/program level type declarations such as structs
	// need to use objects
	public static InstructionDecl setDeclInstruction(Declaration decl) {
	 	InstructionDecl here = new InstructionDecl(decl);
	 	return here;
	}

	public static InstructionTypeDecl setTypeDeclInstruction(TypeDeclaration type) {
		InstructionTypeDecl typeDecl = new InstructionTypeDecl(type);
		for (Declaration d : type.getFields()) {
			LLVMObject field = InstructionTranslator.convertDeclarationToObject(d);
			LLVM.addStruct(type.getName(), field);
		}
		return typeDecl;
	}

	public static void setLocalDeclInstruction(Block b, List<Declaration> locals) {
		for (Declaration d : locals) {
			LLVMObject obj = InstructionTranslator.convertDeclarationToObject(d);
			Register reg = new Register(Register.getNewRegNum(), obj);
			Register.addToRegisters(reg.getRegNum(), reg);
			InstructionAlloca localDecl = new InstructionAlloca(obj, d.getName());
			CFG.addToLocals(d.getName(), obj);	
			b.addInstruction(localDecl);
		}
		CFG.printStructs();
	}

	public static void setLocalParamInstruction(Block b, List<Declaration> params ) {
		for (Declaration param : params) {
			LLVMObject obj = InstructionTranslator.convertDeclarationToObject(param);
			InstructionAlloca pAlloc = new InstructionAlloca(obj, "_P_" + param.getName());
			CFG.addToLocals(param.getName(), obj);
			b.addInstruction(pAlloc);

			Instruction instruction = new InstructionStore("%_P_" + param.getName(), "%"+param.getName());
			b.addInstruction(instruction);
		}
	}

	public static void setFunctionReturnInstruction(Block b, Type returnType) {

		if (!(returnType instanceof VoidType)){
				LLVMObject returnObj = InstructionTranslator.convertTypeToObject(returnType);
				InstructionAlloca returnAlloc = new InstructionAlloca(returnObj, "_retval_");
				b.addInstruction(returnAlloc);
			}
	}

	public static void setReturnInstruction(Block b, LLVMObject type) {
		String returnRegister = Register.getNewRegNum();

		Instruction instr = new InstructionLoad(returnRegister, "%_retval_", type);
		Instruction ret = new InstructionRet(returnRegister, type);
		b.addInstruction(instr);
		b.addInstruction(ret);
	}

	public static void setGuardInstruction(Block curr, Block ifThen, Block ifElse, Expression e, Program p, Function f) {
		String guardReg = InstructionTranslator.parseExpression(curr, e, p, f);
		InstructionBrCond instr = new InstructionBrCond(guardReg, ifThen.getLabel(), ifElse.getLabel());
		curr.addInstruction(instr);
	}

	public static void setWhileGuardInstruction(Block curr, Block join, Block body, Expression e, Program p, Function f) {
		String guardReg = InstructionTranslator.parseExpression(curr, e, p, f);
		InstructionBrCond instr = new InstructionBrCond(guardReg, body.getLabel(), join.getLabel());
		curr.addInstruction(instr);
	}

}