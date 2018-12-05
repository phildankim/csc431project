package cfg;

import ast.*;
import llvm.*;
import cfg.*;

import java.util.*;
import java.io.*;
import java.util.stream.Collectors;


public class CFGBuilder {
 
	private Program p;
	private String filename;
	private HashMap <String, Type> globalSymbolTable = new HashMap<>();
	private HashMap <String, Type> localParamTable = new HashMap<>();
	private HashMap <String, List<Declaration>> funcParamsTable = new HashMap<>();
	private HashMap <String, List<Declaration>> structTable = new HashMap<>();
	private ArrayList<Block> blocks = new ArrayList<Block>();
	private int blockCounter = 0;

	private ArrayList<Instruction> globalDecls = new ArrayList<>();

	private BufferedWriter writer;

	private boolean uce = false;


	public CFGBuilder (Program p, String filename){
		this.p = p;
		this.filename = filename;

	}

	public CFGBuilder (Program p, String filename, boolean uce){
		this.p = p;
		this.filename = filename;
		this.uce = uce;
	}


	public void build() throws IOException{

		BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

		setDeclInstructions();
		setTypeDeclInstructions();

		writer.write("target triple=\"i686\"\n");
		for (Instruction i : globalDecls) {
			writer.write(i.toString()+"\n");
		}

		globalSymbolTable = buildGlobals();
		funcParamsTable = buildFuncParams();
		structTable = buildStructTable();

		for (Function func : p.getFuncs()) {

			// writer.write(buildFuncHeader(func) + "\n");
			// writer.write("{\n");

			localParamTable = new HashMap<>();

			for (Declaration param : func.getParams()) {
				localParamTable.put(param.getName(), param.getType());
			}

			for (Declaration local : func.getLocals()) {
				localParamTable.put(local.getName(), local.getType());
			}

			Block b = buildFunc(func);
			blocks.add(b);

			fixPhis(b);
			//printLLVM(b,writer);



			// writer.write("}\n");

			System.out.println("This is function " + func.getName());
			for (String name : localParamTable.keySet()) {
				System.out.println("\t" + name + ": " + localParamTable.get(name).toString());
			}
		}

		if (uce) {
			eliminateUselessCode();
		}

		for (int i = 0; i < p.getFuncs().size(); i++) {
			writer.write(buildFuncHeader(p.getFuncs().get(i)) + "\n");
			writer.write("{\n");
			printLLVM(blocks.get(i),writer);
			writer.write("}\n");
		}

		writer.write("declare i8* @malloc(i32)\n");
		writer.write("declare void @free(i8*)\n");
		writer.write("declare i32 @printf(i8*, ...)\n");
		writer.write("declare i32 @scanf(i8*, ...)\n");
		writer.write("@.println = private unnamed_addr constant [5 x i8] c\"%ld\\0A\\00\", align 1\n");
		writer.write("@.print = private unnamed_addr constant [5 x i8] c\"%ld \\00\", align 1\n");
		writer.write("@.read = private unnamed_addr constant [4 x i8] c\"%ld\\00\", align 1\n");
		writer.write("@.read_scratch = common global i32 0, align 8\n");

		writer.close();
	}

	public Block buildFunc(Function f) throws IOException {
		Block entryBlock = new Block("LU" + blockCounter++);
		entryBlock.seal();

		// put params inside the block
		for (Declaration param : f.getParams()) {
			entryBlock.writeVariable(param.getName(), new Register (toLLVMObject(param.getType()),param.getName()));
		}

		Block returnBlock = new Block("LU" + blockCounter++);
		Block currentBlock = buildStatement(f.getBody(), entryBlock, returnBlock);

		if (currentBlock != returnBlock) {
			currentBlock.addSucc(returnBlock);
			returnBlock.addPred(currentBlock);

			Instruction branch = new InstructionBr(returnBlock.getLabel());
			currentBlock.addInstruction(branch);
		}
		returnBlock.seal();

		if (f.getType() instanceof VoidType) {
			Instruction retvoid = new InstructionRetVoid();
			returnBlock.addInstruction(retvoid);
		}
		else {
			LLVMObject retType = toLLVMObject(f.getType());
			Value val = returnBlock.readVariable("_retval_", retType);
			Instruction ret = new InstructionRet(val, retType);
			returnBlock.addInstruction(ret);
		}

		return entryBlock;
	}

	public Block buildStatement(Statement s, Block currentBlock, Block returnBlock) {

		if (s instanceof BlockStatement) {

			Block b = currentBlock;

			BlockStatement bs = (BlockStatement)s;

			for (Statement stmt : bs.getStatements()) {
				b = buildStatement(stmt, b, returnBlock);
				if (b == returnBlock) {
					return b;
				}
			}

			return b;
		}

		else if (s instanceof AssignmentStatement) {
			AssignmentStatement as = (AssignmentStatement)s;
			Value sourceRegister = buildExpression(as.getSource(), currentBlock);
			Lvalue target = as.getTarget();
			String id = target.getId();

			if (target instanceof LvalueId) {

				if (localParamTable.containsKey(id)) {
					currentBlock.writeVariable(id, sourceRegister);
				}
				else if (globalSymbolTable.containsKey(id)) {
					LLVMObject type = toLLVMObject(globalSymbolTable.get(id));
					Value result = new Register(type, "@" + id);
					Instruction store = new InstructionStore(result,sourceRegister,type);
					currentBlock.addInstruction(store);
				}
				else throw new RuntimeException("lvalueid missing, AssignmentStatement");
			}
			else if (target instanceof LvalueDot) {
				Expression left = ((LvalueDot)target).getLeft();

				Value leftRegister = buildExpression(left, currentBlock);
				LLVMObject type = leftRegister.getType();

				String field = id;
				String structName = ((StructObject)type).getName();

				Integer fieldIndex = findIndex(structName, field);
				LLVMObject fieldType = findFieldType(structName,field);

				Value forGetElementPtr = new Register(sourceRegister.getType());
				Instruction gep = new InstructionGetElementPtr(forGetElementPtr, type, leftRegister, fieldIndex.toString());
				currentBlock.addInstruction(gep);

				currentBlock.addInstruction(new InstructionStore(forGetElementPtr, sourceRegister,fieldType));

			}

			return currentBlock;
		}

		else if (s instanceof ConditionalStatement) {
			ConditionalStatement cs = (ConditionalStatement)s;

			Value guardRegister = buildExpression(cs.getGuard(), currentBlock);

			Block thenBlock = new Block("LU" + blockCounter++);
			thenBlock.addPred(currentBlock);
			thenBlock.seal();

			Block then = buildStatement(cs.getThen(),thenBlock, returnBlock);

			Block elseBlock = new Block("LU" + blockCounter++);
			elseBlock.addPred(currentBlock);
			elseBlock.seal();

			Block els = buildStatement(cs.getElse(),elseBlock,returnBlock);

			currentBlock.addSucc(thenBlock);
			currentBlock.addSucc(elseBlock);

			if (currentBlock.getLastInstruction() instanceof InstructionIcmp) {
				Instruction br = new InstructionBrCond(guardRegister, thenBlock.getLabel(), elseBlock.getLabel());
				currentBlock.addInstruction(br);			
			}

			else {
				Register truncReg = new Register(new BoolObject());
				InstructionTrunc trunc = new InstructionTrunc(truncReg,guardRegister);
				currentBlock.addInstruction(trunc);
				Instruction br = new InstructionBrCond(truncReg, thenBlock.getLabel(), elseBlock.getLabel());
				currentBlock.addInstruction(br);
			}

			if (then == returnBlock && els == returnBlock) {
				return returnBlock;
			}
			else if (then == returnBlock) {
				return els;
			}
			else if (els == returnBlock) {
				return then;
			}
			else {
				Block join = new Block("LU" + blockCounter++);
				Instruction toJoin = new InstructionBr(join.getLabel());
				join.addPred(then);
				join.addPred(els);
				join.seal();

				then.addSucc(join);
				els.addSucc(join);

				then.addInstruction(toJoin);
				els.addInstruction(toJoin);

				return join;
			}
		}

		else if (s instanceof WhileStatement) {
			WhileStatement ws = (WhileStatement)s;
			Value guardRegister = buildExpression(ws.getGuard(), currentBlock);
			Block body = new Block("LU" + blockCounter++);
			Block join = new Block("LU" + blockCounter++);
			
			// Instruction br = new InstructionBrCond(guardRegister, body.getLabel(), join.getLabel());
			// currentBlock.addInstruction(br);

			if (currentBlock.getLastInstruction() instanceof InstructionIcmp) {
				Instruction br = new InstructionBrCond(guardRegister, body.getLabel(), join.getLabel());
				currentBlock.addInstruction(br);			
			}

			else {
				Register truncReg = new Register(new BoolObject());
				InstructionTrunc trunc = new InstructionTrunc(truncReg,guardRegister);
				currentBlock.addInstruction(trunc);
				Instruction br = new InstructionBrCond(truncReg, body.getLabel(), join.getLabel());
				currentBlock.addInstruction(br);
			}

			Block resultBlock = buildStatement(ws.getBody(), body, returnBlock);

			if (resultBlock != returnBlock) {
				Value guard = buildExpression(ws.getGuard(), resultBlock);
				//Instruction br1 = new InstructionBrCond(guard, body.getLabel(), join.getLabel());

				if (resultBlock.getLastInstruction() instanceof InstructionIcmp) {
					Instruction br1 = new InstructionBrCond(guard, body.getLabel(), join.getLabel());
					resultBlock.addInstruction(br1);			
				}

				else {
					Register truncReg = new Register(new BoolObject());
					InstructionTrunc trunc = new InstructionTrunc(truncReg,guard);
					resultBlock.addInstruction(trunc);
					Instruction br1 = new InstructionBrCond(truncReg, body.getLabel(), join.getLabel());
					resultBlock.addInstruction(br1);
				}

				//resultBlock.addInstruction(br1);
				resultBlock.addSucc(body);
				resultBlock.addSucc(join);
				body.addPred(resultBlock);
				join.addPred(resultBlock);
			}

			currentBlock.addSucc(body);
			currentBlock.addSucc(join);
			body.addPred(currentBlock);
			join.addPred(currentBlock);

			body.seal();
			join.seal();

			return join;
		}

		else if (s instanceof DeleteStatement) {
			DeleteStatement ds = (DeleteStatement)s;


			//	public InstructionBitcast (Value result, Value register, String structName, Boolean isMalloc) {


			Value bitCastThis = buildExpression(ds.getExpression(), currentBlock);

			String structName = "";

			if (bitCastThis instanceof Register) {
				Register btc = (Register)bitCastThis;
				if (btc.getType() instanceof StructObject) {
					StructObject theStruct = (StructObject)btc.getType();
					structName = theStruct.getName();
				}
			}


			Value regForBitcast = new Register(bitCastThis.getType());
			Instruction bitCast = new InstructionBitcast(regForBitcast, bitCastThis, structName,false);
			currentBlock.addInstruction(bitCast);

			Instruction inst = new InstructionFree(regForBitcast);
			currentBlock.addInstruction(inst);

			return currentBlock;
		}

		else if (s instanceof InvocationStatement) {
			InvocationStatement is = (InvocationStatement)s;
			buildExpression(is.getExpression(),currentBlock);

			return currentBlock;
		}

		else if (s instanceof PrintStatement) {
			PrintStatement ps = (PrintStatement)s;
			Value psResult = buildExpression(ps.getExpression(), currentBlock);
			InstructionPrint ip = new InstructionPrint(psResult);
			currentBlock.addInstruction(ip);

			return currentBlock;
		}

		else if (s instanceof PrintLnStatement) {
			PrintLnStatement ps = (PrintLnStatement)s;
			Value psResult = buildExpression(ps.getExpression(), currentBlock);
			InstructionPrintLn ip = new InstructionPrintLn(psResult);
			currentBlock.addInstruction(ip);

			return currentBlock;
		}

		else if (s instanceof ReturnStatement) {
			ReturnStatement rs = (ReturnStatement)s;
			Value returnRegister = buildExpression(rs.getExpression(), currentBlock);
			currentBlock.addSucc(returnBlock);
			returnBlock.addPred(currentBlock);
			currentBlock.writeVariable("_retval_",returnRegister);
			Instruction br = new InstructionBr(returnBlock.getLabel());
			currentBlock.addInstruction(br);

			return returnBlock;
		}

		else if (s instanceof ReturnEmptyStatement) {
			currentBlock.addSucc(returnBlock);
			returnBlock.addPred(currentBlock);
			Instruction br = new InstructionBr(returnBlock.getLabel());
			currentBlock.addInstruction(br);

			return returnBlock;
		}

		throw new RuntimeException("buildstatement undefined for" + s);
	}

	public Value buildExpression(Expression e, Block b) {

		if (e instanceof IntegerExpression) {

			IntegerExpression ie = (IntegerExpression)e;

			IntObject i = new IntObject();
			i.setValue(ie.getValue());

			Immediate immed = new Immediate(ie.getValue(), i);

			return immed;
		}
		else if (e instanceof TrueExpression) {

			IntObject i = new IntObject();
			i.setValue("1");

			Immediate immed = new Immediate("1", i);

			return immed;
		}
		else if (e instanceof FalseExpression) {

			IntObject i = new IntObject();
			i.setValue("0");

			Immediate immed = new Immediate("0", i);

			return immed;
		}

		else if (e instanceof UnaryExpression) {
			UnaryExpression ue = (UnaryExpression)e;

			Value operand = buildExpression(ue.getOperand(), b);

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

					IntObject i = new IntObject();
					i.setValue("1");

					if (b.getLastInstruction() instanceof InstructionIcmp) {
						Register zextReg = new Register(new IntObject());
						InstructionZext zext = new InstructionZext(zextReg, operand, new Immediate("1",i));
						b.addInstruction(zext);

						Register xorReg = new Register(new IntObject());
						Instruction xor = new InstructionXor(xorReg, new Immediate("1",i), zextReg);
						b.addInstruction(xor);
						return xorReg;
					}

					Register reg = new Register(new BoolObject());

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
				System.out.println("GETTING HERE");
				throw new RuntimeException("UNARY ERRORRRR");
			}
		}

		else if (e instanceof IdentifierExpression) {
			IdentifierExpression ie = (IdentifierExpression)e;
			String id = ie.getId();

			if (localParamTable.containsKey(id)) {
				Value val = b.readVariable(id,toLLVMObject(localParamTable.get(id)));
				return val;
			}
			else if (globalSymbolTable.containsKey(id)) {
				Register res = new Register(toLLVMObject(globalSymbolTable.get(id)));
				Register ptr = new Register(toLLVMObject(globalSymbolTable.get(id)), "@" + id);

				Instruction inst = new InstructionLoad(res,ptr,toLLVMObject(globalSymbolTable.get(id)));
				b.addInstruction(inst);

				return res;
			}
			else {
				throw new RuntimeException("id " + id + " not found in localparam or global tables");
			}
		}

		else if (e instanceof BinaryExpression) {
			BinaryExpression be = (BinaryExpression)e;
			Value left = buildExpression(be.getLeft(), b);
			Value right = buildExpression(be.getRight(), b);

			Instruction instr;
			LLVMObject type;
			Value reg;

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
					if (b.getLastInstruction() instanceof InstructionIcmp) {
						// InstructionIcmp icmp = (InstructionIcmp)b.getLastInstruction();
						// Register icmpReg = icmp.getResult();
						IntObject i = new IntObject();
						//Register zextReg = new Register(i);
						InstructionZext zextRight = new InstructionZext(reg, right, new Immediate("1",i));
						b.addInstruction(zextRight);

						Register leftreg = new Register(i);
						InstructionZext zextLeft = new InstructionZext(leftreg, left, new Immediate("1",i));
						b.addInstruction(zextLeft);

						Register orReg = new Register(i);
						instr = new InstructionAnd(orReg, reg, leftreg);
						b.addInstruction(instr);
						return orReg;
					}
					instr = new InstructionAnd(reg, left, right);
					b.addInstruction(instr);
					return reg;
				case OR:
					type = new BoolObject();
					reg = new Register(type);

					if (b.getLastInstruction() instanceof InstructionIcmp) {
						// InstructionIcmp icmp = (InstructionIcmp)b.getLastInstruction();
						// Register icmpReg = icmp.getResult();
						IntObject i = new IntObject();
						//Register zextReg = new Register(i);
						InstructionZext zextRight = new InstructionZext(reg, right, new Immediate("1",i));
						b.addInstruction(zextRight);

						Register leftreg = new Register(i);
						InstructionZext zextLeft = new InstructionZext(leftreg, left, new Immediate("1",i));
						b.addInstruction(zextLeft);

						Register orReg = new Register(i);
						instr = new InstructionOr(orReg, reg, leftreg);
						b.addInstruction(instr);
						return orReg;
					}
					instr = new InstructionOr(reg, left, right);
					b.addInstruction(instr);
					return reg;
				default:
					throw new RuntimeException("invalid binaryexpression type");
			}
		}

		else if (e instanceof InvocationExpression) {
			InvocationExpression ie = (InvocationExpression)e;
			ArrayList<Value> arguments = new ArrayList<Value>();
			// for (Expression arg : ie.getArgs()) {
				
			// 	Value argReg = buildExpression(arg, b);
				
			// 	if (argReg.getName() == "null") {
			// 		NullValue nullVal = (NullValue)argReg;
			// 		nullVal.setType(InstructionTranslator.convertDeclarationToObject(f.getParams().get(i)));
			// 		arguments.add(nullVal);
			// 	}
			// 	else {
			// 		arguments.add(argReg);
			// 	}
			// }

			for (int i = 0; i < ie.getArgs().size(); i++) {
				Expression arg = ie.getArgs().get(i);
				Value argReg = buildExpression(arg, b);
				if (argReg.getName() == "null") {
					NullValue nullVal = (NullValue)argReg;

					String funcCallName = ie.getName();
					List<Declaration> params = funcParamsTable.get(funcCallName);

					nullVal.setType(toLLVMObject(params.get(i).getType()));
					arguments.add(nullVal);
				}
				else {
					arguments.add(argReg);
				}
			}

			LLVMObject retType = toLLVMObject(globalSymbolTable.get(ie.getName()));
			
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

			NewExpression ne = (NewExpression)e;
			String structName = ne.getId();

			Value regForMalloc = new Register(new StructObject(structName));
			Value regForBitcast = new Register(new StructObject(structName));

			// count the number of fields inside the struct:
			int numFields = structTable.get(structName).size();
			int bytesToAllocate = 0;

			bytesToAllocate = numFields * 4;
			Instruction ma = new InstructionMalloc(regForMalloc,bytesToAllocate);
			b.addInstruction(ma);

			Instruction bc = new InstructionBitcast(regForBitcast,regForMalloc,structName, true);
			b.addInstruction(bc);

			return regForBitcast;

		}

		else if (e instanceof DotExpression) {
			DotExpression de = (DotExpression)e;

			/* OLD STUFF 
			String dotId = de.getId();

			Value leftReg = buildExpression(de.getLeft(), b);
			StructObject leftType = (StructObject)leftReg.getType();

			LLVMObject idStruct = LLVM.getStructField(leftType.getName(), dotId);

			Value idRes = new Register(idStruct);

			int index = LLVM.getFieldIndex(leftType.getName(), de.getId());

			InstructionGetElementPtr gep = new InstructionGetElementPtr(idRes, leftType, leftReg, Integer.toString(index));
			b.addInstruction(gep);

			Value result = new Register(idRes.getType());
			InstructionLoad il = new InstructionLoad(result, idRes, result.getType());
			b.addInstruction(il);
			return result;

			


				Value leftRegister = buildExpression(de.getLeft(), b);
				LLVMObject type = leftRegister.getType();

				String field = de.getId();
				String structName = ((StructObject)type).getName();

				Integer fieldIndex = findIndex(structName, field);

				Value forGetElementPtr = new Register(sourceRegister.getType());
				Instruction gep = new InstructionGetElementPtr(forGetElementPtr, type, leftRegister, fieldIndex.toString());
				b.addInstruction(gep);

				b.addInstruction(new InstructionStore(forGetElementPtr, sourceRegister,forGetElementPtr.getType()));

			*/


			Value left = buildExpression(de.getLeft(), b);

			String structName = ((StructObject)left.getType()).getName();
			String fieldName = de.getId();
			Integer fieldIndex = findIndex(structName, fieldName);
			LLVMObject fieldType = findFieldType(structName,fieldName);

			Value forField = new Register(fieldType);
			Instruction gep = new InstructionGetElementPtr(forField, left.getType(), left, fieldIndex.toString());
			b.addInstruction(gep);

			Value forLoad = new Register(fieldType);
			Instruction il = new InstructionLoad(forLoad, forField, fieldType);
			b.addInstruction(il);

			return forLoad;
		}

		else if (e instanceof ReadExpression) {
			Value readScratch = new Register(new IntObject(),"@.read_scratch");
			InstructionScan ir = new InstructionScan(readScratch);
			b.addInstruction(ir);

			Value result = new Register(new IntObject());
			InstructionLoad il = new InstructionLoad(result,readScratch,new IntObject());
			b.addInstruction(il);

			return result;
		}

		else if (e instanceof NullExpression) {
				return new NullValue();
			}


		throw new RuntimeException ("buildexpression error: should not reach here " + e.toString());
	}

	public void printLLVM (Block b, BufferedWriter writer) throws IOException{

		Set<Block> visited = new HashSet<>();
    	Queue<Block> queue = new ArrayDeque<>();
    	visited.add(b);
    	queue.add(b);
        while (queue.size() > 0) {
            Block cur = queue.poll();
            List<Block> newSuccessors = cur.getSuccessors().stream()
                    .filter(successor -> !visited.contains(successor))
                    .collect(Collectors.toList());
            queue.addAll(newSuccessors);
            visited.addAll(newSuccessors);
            cur.printSSA(writer);
        }
    }
	public Integer findIndex(String structName, String fieldName) {
		List<Declaration> fields = structTable.get(structName);
		if (fields != null) {
			for (int i = 0; i < fields.size(); i++) {
				if (fields.get(i).getName().equals(fieldName)) {
					return i;
				}
			}
			throw new RuntimeException("found the structname " + structName + " but cant find field " + fieldName);
		}
		else {
			throw new RuntimeException("did not find structname " + structName + " in structtable");
		}
	}

	public LLVMObject findFieldType(String structName, String fieldName) {
		List<Declaration> fields = structTable.get(structName);
		if (fields != null) {
			for (int i = 0; i < fields.size(); i++) {
				if (fields.get(i).getName().equals(fieldName)) {
					return toLLVMObject(fields.get(i).getType());
				}
			}
			throw new RuntimeException("found the structname " + structName + " but cant find field " + fieldName);
		}
		else {
			throw new RuntimeException("did not find structname " + structName + " in structtable");
		}
	}

	public HashMap <String, List<Declaration>> buildStructTable() {
		HashMap <String, List<Declaration>> structTable = new HashMap<>();

		for (TypeDeclaration struct: p.getTypes()) {
			structTable.put(struct.getName(), struct.getFields());
		}

		return structTable;
	}

	public HashMap <String, List<Declaration>> buildFuncParams() {
		HashMap <String, List<Declaration>> funcTable = new HashMap<>();

		for (Function func : p.getFuncs()) {
			funcTable.put(func.getName(), func.getParams());
		}

		return funcTable;
	}

	public HashMap<String,Type> buildGlobals() {
		HashMap <String, Type> gMap = new HashMap<>();

		for (Declaration decl : p.getDecls()) {
			gMap.put(decl.getName(), decl.getType());
		}

		for (Function func : p.getFuncs()) {
			gMap.put(func.getName(), func.getType());
		}

		return gMap;
	}

	public static LLVMObject toLLVMObject (Type t) {
		if (t instanceof IntType) {
			return new IntObject();
		}
		else if (t instanceof BoolType) {
			return new BoolObject();
		}
		else if (t instanceof VoidType) {
			return new VoidObject();
		}
		else if (t instanceof StructType) {
			return new StructObject(((StructType)t).getName());
		}
		else throw new RuntimeException("not here brooooo");
	}

	public void setDeclInstructions() {
		for (Declaration d : this.p.getDecls()) {
	 		this.globalDecls.add(InstructionTranslator.setDeclInstruction(d));
		}
	}

	public void setTypeDeclInstructions() {
		for (TypeDeclaration td : this.p.getTypes()) {
			this.globalDecls.add(InstructionTranslator.setTypeDeclInstruction(td));
		}
	}

	public String buildFuncHeader(Function f) {
		String header = "define ";

		if (f.getType() instanceof IntType || f.getType() instanceof BoolType) {
				header += "i32";
		}
		else if (f.getType() instanceof VoidType){
			header += "void";
		}
		else if (f.getType() instanceof StructType) {
			StructType st = (StructType)f.getType();
			header += "%struct." + st.getName() + "*";
		}
		else {
			throw new RuntimeException("building func header but return is not int or bool or void or struct");
		}

		header += " @" + f.getName() + "(";

		List<Declaration> params = f.getParams();

		for (int i = 0; i < params.size(); i++) {
			Declaration currDec = params.get(i);

			if (currDec.getType() instanceof IntType || currDec.getType() instanceof BoolType) {
				header += "i32 %" + currDec.getName();
			}
			else if (currDec.getType() instanceof StructType) {
				StructType st = (StructType)currDec.getType();
				header += "%struct." + st.getName() + "* %" + currDec.getName();
			}


			if (i != (params.size() - 1)) {
				header += ", ";
			}
		}

		header += ")";

		return header;
	}


	public void eliminateUselessCode() {
		System.out.println("ELIMINATING USELESS CODEEEEE");

		HashMap<Register,Instruction> definitions = new HashMap<>();

		boolean somethingChanged = true;
		while (somethingChanged) {

			somethingChanged = false;
			definitions = new HashMap<>();

			// gather all definitions
			for (Block b: blocks) {

				Set<Block> visited = new HashSet<>();
		    	Queue<Block> queue = new ArrayDeque<>();
		    	visited.add(b);
		    	queue.add(b);
		        while (queue.size() > 0) {
		            Block current = queue.poll();
		            List<Block> newSuccessors = current.getSuccessors().stream()
		                    .filter(successor -> !visited.contains(successor))
		                    .collect(Collectors.toList());
		            queue.addAll(newSuccessors);
		            visited.addAll(newSuccessors);

		            definitions.putAll(getDefinitions(current));
		        }
			}

			// delete all registers from definitions that get used
			for (Block b: blocks) {

				Set<Block> visited = new HashSet<>();
		    	Queue<Block> queue = new ArrayDeque<>();
		    	visited.add(b);
		    	queue.add(b);
		        while (queue.size() > 0) {
		            Block current = queue.poll();
		            List<Block> newSuccessors = current.getSuccessors().stream()
		                    .filter(successor -> !visited.contains(successor))
		                    .collect(Collectors.toList());
		            queue.addAll(newSuccessors);
		            visited.addAll(newSuccessors);

		            protectUsedRegisters(definitions, current);
		        }
			}

			// delete instructions with definitions that dont get used
			for (Block b: blocks) {

				Set<Block> visited = new HashSet<>();
		    	Queue<Block> queue = new ArrayDeque<>();
		    	visited.add(b);
		    	queue.add(b);
		        while (queue.size() > 0) {
		            Block current = queue.poll();
		            List<Block> newSuccessors = current.getSuccessors().stream()
		                    .filter(successor -> !visited.contains(successor))
		                    .collect(Collectors.toList());
		            queue.addAll(newSuccessors);
		            visited.addAll(newSuccessors);

		            somethingChanged = deleteInstructions(definitions, current);
		        }
			}
		}
		printDefinitions(definitions);
	}

	public boolean deleteInstructions(HashMap<Register, Instruction> definitions, Block b) {
		boolean somethingChanged = false;
		return somethingChanged;
	}

	public void protectUsedRegisters(HashMap<Register, Instruction> definitions, Block b) {

		ArrayList<InstructionPhi> phiInstructions = b.phiInstructions;
		ArrayList<Instruction> instructions = b.instructions;

		for (InstructionPhi phiI : phiInstructions) {
			ArrayList<PhiOperand> phiOperands = phiI.phiOperands;
			for (PhiOperand po : phiOperands) {
				if (po.value instanceof Register) {
					Register used = (Register)po.value;
					definitions.remove(used);
				}
			}
		}

		for (Instruction i : instructions) {
			if (i instanceof InstructionAdd) {
				InstructionAdd ia = (InstructionAdd)i;
				if (ia.operand1 instanceof Register){
					Register used = (Register)ia.operand1;
					definitions.remove(used);
				}
				if (ia.operand2 instanceof Register){
					Register used = (Register)ia.operand2;
					definitions.remove(used);
				}
			}
			else if (i instanceof InstructionAlloca) {
				// do nothing, alloca only has definitions
			}
			else if (i instanceof InstructionAnd) {
				InstructionAnd ia = (InstructionAnd)i;
				if (ia.operand1 instanceof Register){
					Register used = (Register)ia.operand1;
					definitions.remove(used);
				}
				if (ia.operand2 instanceof Register){
					Register used = (Register)ia.operand2;
					definitions.remove(used);
				}
			}

			else throw new RuntimeException("protectUsedRegisters error: undefined instruction " + i.toString());
		}

	}

	public void printDefinitions(HashMap<Register, Instruction> definitions) {
		for (Register reg : definitions.keySet()) {
			System.out.println(reg.toString() + " is in " + definitions.get(reg).toString());
		}
	}

	public HashMap<Register, Instruction> getDefinitions(Block b) {

		ArrayList<InstructionPhi> phiInstructions = b.phiInstructions;
		ArrayList<Instruction> instructions = b.instructions;

		HashMap<Register,Instruction> defMap = new HashMap<Register, Instruction>();

		for (InstructionPhi phiI : phiInstructions) {

			if (phiI.register instanceof Register) {
				Register key = (Register)phiI.register;
				Instruction val = phiI;

				defMap.put(key, val);
			}
		}

		for (Instruction i : instructions) {

			if (i instanceof InstructionAdd) {
				InstructionAdd ia = (InstructionAdd)i;
				if (ia.register instanceof Register) {
					Register key = (Register)ia.register;
					defMap.put(key,i);
				}
			}
			else if (i instanceof InstructionAlloca) {
				InstructionAlloca ia = (InstructionAlloca)i;
				if (ia.result instanceof Register) {
					Register key = (Register)ia.result;
					defMap.put(key,i);
				}
			}
			else if (i instanceof InstructionAnd) {
				InstructionAnd ia = (InstructionAnd)i;
				if (ia.register instanceof Register) {
					Register key = (Register)ia.register;
					defMap.put(key,i);
				}
			}
			else if (i instanceof InstructionBitcast) {
				InstructionBitcast ib = (InstructionBitcast)i;
				if (ib.result instanceof Register) {
					Register key = (Register)ib.result;
					defMap.put(key,i);
				}
			}
			else if (i instanceof InstructionBr) {
				// DO NOTHING - BRANCH INSTRUCTIONS HAVE NO DEFS
			}

			else if (i instanceof InstructionBrCond) {
				// DO NOTHING - BRANCH INSTRUCTIONS HAVE NO DEFS
			}

			else if (i instanceof InstructionCall) {
				// do nothing - calls are all uses
			}

			else if (i instanceof InstructionDecl) {
				// DO NOTHING - THESE ARE GLOBAL DECLARATIONS
			}

			else if (i instanceof InstructionFree) {
				// DO NOTHING - FREE IS CALL VOID WHICH IS A USE
			}
			else if (i instanceof InstructionGetElementPtr) {
				InstructionGetElementPtr ig = (InstructionGetElementPtr)i;
				if (ig.result instanceof Register) {
					Register key = (Register)ig.result;
					defMap.put(key,i);
				}
			}

			else if (i instanceof InstructionIcmp) {
				InstructionIcmp ic = (InstructionIcmp)i;
				if (ic.result instanceof Register) {
					Register key = (Register)ic.result;
					defMap.put(key,i);
				}
			}

			else if (i instanceof InstructionLoad) {
				InstructionLoad il = (InstructionLoad)i;
				if (il.result instanceof Register) {
					Register key = (Register)il.result;
					defMap.put(key,i);
				}
			}

			else if (i instanceof InstructionMalloc) {
				InstructionMalloc im = (InstructionMalloc)i;
				if (im.register instanceof Register) {
					Register key = (Register)im.register;
					defMap.put(key,i);
				}
			}

			else if (i instanceof InstructionMul) {
				InstructionMul im = (InstructionMul)i;
				if (im.register instanceof Register) {
					Register key = (Register)im.register;
					defMap.put(key,i);
				}
			}

			else if (i instanceof InstructionOr) {
				InstructionOr io = (InstructionOr)i;
				if (io.register instanceof Register) {
					Register key = (Register)io.register;
					defMap.put(key,i);
				}
			}

			else if (i instanceof InstructionPhi) {
				throw new RuntimeException("WTH is a PhiInstruction doing in the regular instructions list");
			}

			else if (i instanceof InstructionPrint) {
				// do nothing, print only has a use but no def
			}

			else if (i instanceof InstructionPrintLn) {
				// do nothing print only has a use but no def
			}

			else if (i instanceof InstructionRet) {
				// ret only has a use no def
			}

			else if (i instanceof InstructionRetVoid) {
				// retvoid only has use no def
			}
			else if (i instanceof InstructionScan) {
				// scan only has a use
			}
			else if (i instanceof InstructionSdiv) {
				InstructionSdiv is = (InstructionSdiv)i;
				if (is.register instanceof Register) {
					Register key = (Register)is.register;
					defMap.put(key,i);
				}
			}

			else if (i instanceof InstructionStore) {
				InstructionStore is = (InstructionStore)i;
				if (is.pointer instanceof Register) {
					Register key = (Register)is.pointer;
					defMap.put(key,i);
				}
			}
			else if (i instanceof InstructionStub) {
				throw new RuntimeException("WHYS IS THERE A STUB IN the intructionlist");
			}
			else if (i instanceof InstructionSub) {
				InstructionSub is = (InstructionSub)i;
				if (is.register instanceof Register) {
					Register key = (Register)is.register;
					defMap.put(key,i);
				}
			}

			else if (i instanceof InstructionTrunc) {
				InstructionTrunc it = (InstructionTrunc)i;
				if (it.result instanceof Register) {
					Register key = (Register)it.result;
					defMap.put(key,i);
				}
			}
			else if (i instanceof InstructionTypeDecl) {
				// type declarations are struct declarations. it's fine
			}
			else if (i instanceof InstructionXor) {
				InstructionXor ix = (InstructionXor)i;
				if (ix.register instanceof Register) {
					Register key = (Register)ix.register;
					defMap.put(key,i);
				}
			}
			else if (i instanceof InstructionZext) {
				InstructionZext iz = (InstructionZext)i;
				if (iz.register instanceof Register) {
					Register key = (Register)iz.register;
					defMap.put(key,i);
				}
			}
			else throw new RuntimeException("UCE getDef error: unknown behavior for " + i.toString());
		}



		return defMap;
	}

	public void fixPhis(Block b) {

	}
}