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

			Value parseVal = InstructionTranslator.parseExpression(b, ds.getExpression(),p, f);
			Register regForLoad = (Register)parseVal;
			StructObject so = (StructObject)regForLoad.getType();
			structName = so.getName();
			Value regForBitcast = new Register(new StructObject(so.getName()));

			Instruction bc = new InstructionBitcast(regForBitcast,regForLoad,structName, false);
			b.addInstruction(bc);

			Instruction callvoid = new InstructionFree(regForBitcast);
			b.addInstruction(callvoid);
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

			// if not in function (not local), then check global
			if (type == null) {
				type = LLVM.getType(id);
				Register globalReg = new Register(type, "@" + id);
				Register reg = new Register(type);
				InstructionLoad load = new InstructionLoad(reg, globalReg, type);
				b.addInstruction(load);
				return reg;
			}

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

		else if (e instanceof TrueExpression) {
			return new Immediate("1");
		}

		else if (e instanceof FalseExpression) {
			return new Immediate("0");
		}

		else if (e instanceof NullExpression) {
			return new NullValue();
		}

		else if (e instanceof UnaryExpression) {
			UnaryExpression ue = (UnaryExpression)e;

			Value operand = InstructionTranslator.parseExpression(b, ue.getOperand(), p, f);

			if (ue.getOperator().equals(UnaryExpression.Operator.NOT)) {
				if (operand instanceof Immediate) {
					Immediate immed = (Immediate)operand;
					if (immed.getValue().equals("0")) {
						IntObject i = new IntObject();
						i.setValue("1");
						return new Immediate("1", i);
					}
					else if (immed.getValue().equals("1")) {
						IntObject i = new IntObject();
						i.setValue("0");
						return new Immediate("0", i);
					}
					else {
						throw new RuntimeException("Unary Not, operand is immediate but not 0 or 1");
					}
				}
				else if (operand instanceof Register) {
					Register reg = new Register(new BoolObject());

					IntObject i = new IntObject();
					i.setValue("1");

					Instruction xor = new InstructionXor(reg, new Immediate("1",i), operand);
					b.addInstruction(xor);

					return reg;
				}
			}
			else if (ue.getOperator().equals(UnaryExpression.Operator.MINUS)) {
				if (operand instanceof Immediate) {
					Immediate immed = (Immediate)operand;
					IntObject i = new IntObject();
					Integer newValue = Integer.parseInt(immed.getValue());
					i.setValue(Integer.toString(-newValue));
					return new Immediate(i.getValue(),i);

				}
				else if (operand instanceof Register) {
					throw new RuntimeException("unary minus but register");
				}
				else throw new RuntimeException("unary minus" + ue.getOperator().toString());
			}

			else {
				throw new RuntimeException("UNARY ERRORRRR");
			}
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
					type = new BoolObject("i1");
					reg = new Register(type);
					instr = new InstructionIcmp(reg, "slt", left, right);
					b.addInstruction(instr);
					return reg;
				case GT:
					type = new BoolObject("i1");
					reg = new Register(type);
					instr = new InstructionIcmp(reg, "sgt", left, right);
					b.addInstruction(instr);
					return reg;
				case GE:
					type = new BoolObject("i1");
					reg = new Register(type);
					instr = new InstructionIcmp(reg, "sge", left, right);
					b.addInstruction(instr);
					return reg;
				case LE:
					type = new BoolObject("i1");
					reg = new Register(type);
					instr = new InstructionIcmp(reg, "sle", left, right);
					b.addInstruction(instr);
					return reg;
				case EQ:
					type = new BoolObject("i1");
					reg = new Register(type);
					instr = new InstructionIcmp(reg, "eq", left, right);
					b.addInstruction(instr);
					return reg;
				case NE:
					type = new BoolObject("i1");
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
					// if (t instanceof IntType) {
					// 	retType = new IntObject();
					// }
					// else if (t instanceof BoolType) {
					// 	retType = new BoolObject();
					// }
					// else {
					// 	retType = new VoidObject();
					// }

					retType = convertTypeToObject(t);
				}
			}
			Register result = new Register(retType);
			// if not void, then store in register and return reg
			if (!(retType instanceof VoidObject)) {
				InstructionCall ic = new InstructionCall(result, retType, ie.getName(), arguments);
				b.addInstruction(ic);
			}
			else {
				InstructionCall ic = new InstructionCall(retType, ie.getName(), arguments);
				b.addInstruction(ic);
			}
			return result;
		}

		else if (e instanceof NewExpression) {
			// fix this shit
			NewExpression ne = (NewExpression)e;
			String structName = ne.getId();
			Value regForMalloc = new Register(new StructObject(structName));
			Value regForBitcast = new Register(new StructObject(structName));

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

			Register loadReg = new Register(idStruct);
			InstructionLoad load = new InstructionLoad(loadReg, idRes, idStruct);
			b.addInstruction(load);

			return loadReg;
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

			for (Declaration param : f.getParams()) {
				if (id.equals(param.getName())) {
					Register paramId = new Register(type,"_P_" + id);
					return paramId;
				}
 			}

 			for (Declaration global : p.getDecls()) {
 				if (id.equals(global.getName())) {
 					Register globalVar = new Register(type, "@" + id);
 					return globalVar;
 				}
 			}
			
			return new Register(type,id);
		}
		else { // otherwise, it's an lvaluedot
			LvalueDot lvdot = (LvalueDot)lv;	
			String lvId = lvdot.getId();
			Value regLeftNum = InstructionTranslator.parseExpression(b, lvdot.getLeft(), p, f);
			StructObject regObject = (StructObject)regLeftNum.getType();

			
			LLVMObject idObj = LLVM.getStructField(regObject.getName(), lvId);
			Register idRes = new Register(idObj); //automatically adds to hashmap

			int index = LLVM.getFieldIndex(regObject.getName(), lvId);

			Register gepReg = new Register(idObj);

			// this instruction needs to be preceded by a LOAD
			// refer to domath() in mixed.ll
			
			InstructionGetElementPtr gep = new InstructionGetElementPtr(gepReg, regObject, regLeftNum, Integer.toString(index));
			b.addInstruction(gep);

			return gepReg;
		}
	}

	// global/program level type declarations such as structs
	// need to use objects
	public static InstructionDecl setDeclInstruction(Declaration decl) {
	 	InstructionDecl here = new InstructionDecl(decl);
	 	LLVMObject obj = InstructionTranslator.convertDeclarationToObject(decl);
		//InstructionAlloca localDecl = new InstructionAlloca(obj, new Register(obj, d.getName()));
		LLVM.addToGlobals(decl.getName(), obj);	
		//b.addInstruction(localDecl);
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

		if (curr.getLastInstruction() instanceof InstructionIcmp) {
			InstructionBrCond instr = new InstructionBrCond(guardReg, ifThen.getLabel(), ifElse.getLabel());
			curr.addInstruction(instr);	
		}
		else {
			Register truncReg = new Register(new BoolObject());
			InstructionTrunc trunc = new InstructionTrunc(truncReg, guardReg);
			curr.addInstruction(trunc);
			InstructionBrCond instr = new InstructionBrCond(truncReg, ifThen.getLabel(), ifElse.getLabel());
			curr.addInstruction(instr);
		}
		
	}

	public static void setWhileGuardInstruction(Block curr, Block join, Block body, Expression e, Program p, Function f) {
		Value guardReg = InstructionTranslator.parseExpression(curr, e, p, f);

		if (curr.getLastInstruction() instanceof InstructionIcmp) {
			InstructionBrCond instr = new InstructionBrCond(guardReg, body.getLabel(), join.getLabel());
			curr.addInstruction(instr);
		}
		else {
			Register truncReg = new Register(new BoolObject());
			InstructionTrunc trunc = new InstructionTrunc(truncReg, guardReg);
			curr.addInstruction(trunc);
			InstructionBrCond instr = new InstructionBrCond(truncReg, body.getLabel(), join.getLabel());
			curr.addInstruction(instr);
		}
		
	}

}