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
			
			Value target = InstructionTranslator.parseLvalue(b, as.getTarget(), p, f);

			if (as.getSource() instanceof ReadExpression) {
				InstructionScan ir = new InstructionScan(target);
				b.addInstruction(ir);
			}
			else {

				Value source = InstructionTranslator.parseExpression(b, as.getSource(), p, f);

				InstructionStore instr = new InstructionStore(target, source, source.getType());
				b.addInstruction(instr);
			}
		}

		else if (s instanceof InvocationStatement) {
			InvocationStatement is = (InvocationStatement)s;
			InstructionTranslator.parseExpression(b, is.getExpression(), p, f);
		}

		else if (s instanceof PrintStatement) {
			PrintStatement ps = (PrintStatement)s;
			Value psResult = InstructionTranslator.parseExpression(b, ps.getExpression(), p, f);
			InstructionPrint ip = new InstructionPrint(psResult);
			b.addInstruction(ip);
		}

		else if (s instanceof PrintLnStatement) {
			PrintLnStatement ps = (PrintLnStatement)s;
			Value psResult = InstructionTranslator.parseExpression(b, ps.getExpression(), p, f);
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


			Value regForLoad = InstructionTranslator.parseExpression(b, ds.getExpression(),p, f);
			Value regForBitcast = new Register(new StructObject(structName),structName);

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
	public static Value parseExpression(Block b, Expression e, Program p, Function f) {

		if (e instanceof IntegerExpression) {

			IntegerExpression ie = (IntegerExpression)e;

			IntObject i = new IntObject();
			i.setValue(ie.getValue());

			Immediate immed = new Immediate(ie.getValue(), i);

			return immed;
		}

		else if (e instanceof IdentifierExpression) {
			IdentifierExpression ie = (IdentifierExpression)e;

			String id = ie.getId();
			LLVMObject type = CFG.getType(id);

			Register reg = new Register(type);

			InstructionLoad load;

			for (Declaration param : f.getParams()) {
				if (id.equals(param.getName())) {
					load = new InstructionLoad(reg, new Register(type,"_P_" + id), type);
					b.addInstruction(load);
					return reg;
				}
 			}

			load = new InstructionLoad(reg, new Register(type,id), type);
			b.addInstruction(load);

			return reg;
		}

		else if (e instanceof BinaryExpression) {
			BinaryExpression be = (BinaryExpression)e;
			Value left = parseExpression(b, be.getLeft(), p, f);
			Value right = parseExpression(b, be.getRight(), p, f);

			Instruction instr;
			LLVMObject type;
			Register reg;

			switch (be.getOperator()) {
				case PLUS:
					type = new IntObject();
					reg = new Register(type);
					instr = new InstructionAdd(reg, left, right);
					b.addInstruction(instr);
					return reg;
				case MINUS:
					type = new IntObject();
					reg = new Register(type);
					instr = new InstructionSub(reg, left, right);
					b.addInstruction(instr);
					return reg;
				case DIVIDE:
					type = new IntObject();
					reg = new Register(type);
					instr = new InstructionSdiv(reg, left, right);
					b.addInstruction(instr);
					return reg;
				case TIMES:
					type = new IntObject();
					reg = new Register(type);
					instr = new InstructionMul(reg, left, right);
					b.addInstruction(instr);
					return reg;
				case LT:
					type = new IntObject();
					reg = new Register(type);
					instr = new InstructionIcmp(reg, "slt", left, right);
					b.addInstruction(instr);
					return reg;
				case GT:
					type = new IntObject();
					reg = new Register(type);
					instr = new InstructionIcmp(reg, "sgt", left, right);
					b.addInstruction(instr);
					return reg;
				case GE:
					type = new IntObject();
					reg = new Register(type);
					instr = new InstructionIcmp(reg, "sge", left, right);
					b.addInstruction(instr);
					return reg;
				case LE:
					type = new IntObject();
					reg = new Register(type);
					instr = new InstructionIcmp(reg, "sle", left, right);
					b.addInstruction(instr);
					return reg;
				case EQ:
					type = new BoolObject();
					reg = new Register(type);
					instr = new InstructionIcmp(reg, "eq", left, right);
					b.addInstruction(instr);
					return reg;
				case NE:
					type = new BoolObject();
					reg = new Register(type);
					instr = new InstructionIcmp(reg, "ne", left, right);
					b.addInstruction(instr);
					return reg;
				case AND:
					type = new BoolObject();
					reg = new Register(type);
					instr = new InstructionAnd(reg, left, right);
					b.addInstruction(instr);
					return reg;
				case OR:
					type = new BoolObject();
					reg = new Register(type);
					instr = new InstructionOr(reg, left, right);
					b.addInstruction(instr);
					return reg;
				default:
					return null;
			}
		}

		else if (e instanceof InvocationExpression) {
			InvocationExpression ie = (InvocationExpression)e;
			ArrayList<Value> arguments = new ArrayList<Value>();
			for (Expression arg : ie.getArgs()) {
				Value argReg = InstructionTranslator.parseExpression(b,arg, p, f);
				arguments.add(argReg);
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
				Register result = new Register(retType);
				InstructionCall ic = new InstructionCall(result, retType, ie.getName(), arguments);
				b.addInstruction(ic);
				return result;
			}
			else {
				InstructionCall ic = new InstructionCall(retType, ie.getName(), arguments);
				b.addInstruction(ic);
			}
		}

		else if (e instanceof NewExpression) {

			NewExpression ne = (NewExpression)e;
			String structName = ne.getId();

			Value regForMalloc = new Register(new StructObject(structName));
			Value regForBitcast = new Register(new StructObject(structName));

			System.out.println("ne: " + structName);

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

			return regForBitcast;

		}

		else if (e instanceof DotExpression) {
			DotExpression de = (DotExpression)e;
			String dotId = de.getId();

			Value leftReg = InstructionTranslator.parseExpression(b, de.getLeft(), p, f);
			StructObject leftType = (StructObject)leftReg.getType();

			LLVMObject idStruct = LLVM.getStructField(leftType.getName(), dotId);

			Value idRes = new Register(idStruct);

			int index = LLVM.getFieldIndex(leftType.getName(), de.getId());

			InstructionGetElementPtr gep = new InstructionGetElementPtr(idRes, leftType, leftReg, Integer.toString(index));
			b.addInstruction(gep);
			return idRes;
		}

		return null;
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

	public static Value parseLvalue(Block b, Lvalue lv, Program p, Function f) {
		if (lv instanceof LvalueId) {
			LvalueId lvid = (LvalueId)lv;
			String id = lvid.getId();
			LLVMObject type = CFG.getType(id);

			// InstructionStore store = new InstructionStore(regNum, "%" + id, type);
			// b.addInstruction(store);
			return new Register(type,id);
		}
		else { // otherwise, it's an lvaluedot
			LvalueDot lvdot = (LvalueDot)lv;	
			String lvId = lvdot.getId();

			Value regLeft = InstructionTranslator.parseExpression(b, lvdot.getLeft(), p, f);

			StructObject dotType = (StructObject)regLeft.getType();
			Register dotRes = new Register(dotType);

			LLVMObject idObj = LLVM.getStructField(dotType.getName(), lvId);
			Register idRes = new Register(idObj);

			int index = LLVM.getFieldIndex(dotType.getName(), lvId);

			// instruction to load dottype into new register
			InstructionLoad load = new InstructionLoad(dotRes, regLeft, dotType);
			b.addInstruction(load);

			InstructionGetElementPtr gep = new InstructionGetElementPtr(idRes, dotType, regLeft, Integer.toString(index));
			b.addInstruction(gep);

			return idRes;
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
			InstructionAlloca localDecl = new InstructionAlloca(obj, new Register(obj, d.getName()));
			CFG.addToLocals(d.getName(), obj);	
			b.addInstruction(localDecl);
		}
		CFG.printStructs();
	}

	public static void setLocalParamInstruction(Block b, List<Declaration> params ) {
		for (Declaration param : params) {
			LLVMObject obj = InstructionTranslator.convertDeclarationToObject(param);

			Register paramReg = new Register(obj, "_P_" + param.getName());
			Register reg = new Register(obj, param.getName());

			InstructionAlloca pAlloc = new InstructionAlloca(obj, paramReg);
			CFG.addToLocals(param.getName(), obj);
			b.addInstruction(pAlloc);



			Instruction instruction = new InstructionStore(paramReg, reg);
			b.addInstruction(instruction);
		}
	}

	public static void setFunctionReturnInstruction(Block b, Type returnType) {

		if (!(returnType instanceof VoidType)){
				LLVMObject returnObj = InstructionTranslator.convertTypeToObject(returnType);
				InstructionAlloca returnAlloc = new InstructionAlloca(returnObj, new Register(returnObj, "_retval_"));
				b.addInstruction(returnAlloc);
			}
	}

	public static void setReturnInstruction(Block b, LLVMObject type) {
		Register returnRegister = new Register(type);

		Instruction instr = new InstructionLoad(returnRegister, new Register(type,"_retval_"), type);
		Instruction ret = new InstructionRet(returnRegister, type);
		b.addInstruction(instr);
		b.addInstruction(ret);
	}

	public static void setGuardInstruction(Block curr, Block ifThen, Block ifElse, Expression e, Program p, Function f) {
		Value guardReg = InstructionTranslator.parseExpression(curr, e, p, f);
		InstructionBrCond instr = new InstructionBrCond(guardReg, ifThen.getLabel(), ifElse.getLabel());
		curr.addInstruction(instr);
	}

	public static void setWhileGuardInstruction(Block curr, Block join, Block body, Expression e, Program p, Function f) {
		Value guardReg = InstructionTranslator.parseExpression(curr, e, p, f);
		InstructionBrCond instr = new InstructionBrCond(guardReg, body.getLabel(), join.getLabel());
		curr.addInstruction(instr);
	}

}