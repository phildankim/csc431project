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


	public CFGBuilder (Program p, String filename){
		this.p = p;
		this.filename = filename;

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

			writer.write(buildFuncHeader(func) + "\n");
			writer.write("{\n");

			localParamTable = new HashMap<>();

			for (Declaration param : func.getParams()) {
				localParamTable.put(param.getName(), param.getType());
			}

			for (Declaration local : func.getLocals()) {
				localParamTable.put(local.getName(), local.getType());
			}

			Block b = buildFunc(func);
			blocks.add(b);

			Set<Block> visited = new HashSet<>();
        	Queue<Block> queue = new ArrayDeque<>();
        	visited.add(b);
        	queue.add(b);
	        while (queue.size() > 0) {
	            Block cur = queue.poll();
	            List<Block> newSuccessors = cur.getSuccessors().stream()
	                    .filter(successor -> !visited.contains(successor))
	                    //.filter(successor -> successor != returnBlock)
	                    .collect(Collectors.toList());
	            queue.addAll(newSuccessors);
	            visited.addAll(newSuccessors);
	            cur.printSSA(writer);
	        }

			writer.write("}\n");

			System.out.println("This is function " + func.getName());
			for (String name : localParamTable.keySet()) {
				System.out.println("\t" + name + ": " + localParamTable.get(name).toString());
			}
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
			Instruction ret = new InstructionRet(val);
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
					Value result = new Register(type);
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

			Instruction br = new InstructionBrCond(guardRegister, thenBlock.getLabel(), elseBlock.getLabel());
			currentBlock.addInstruction(br);

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
			Instruction br = new InstructionBrCond(guardRegister, body.getLabel(), join.getLabel());
			currentBlock.addInstruction(br);

			Block resultBlock = buildStatement(ws.getBody(), body, returnBlock);

			if (resultBlock != returnBlock) {
				Value guard = buildExpression(ws.getGuard(), resultBlock);
				Instruction br1 = new InstructionBrCond(guard, body.getLabel(), join.getLabel());
				resultBlock.addInstruction(br1);
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

			if (ue.getOperator().equals(UnaryExpression.Operator.NOT)) {
				throw new RuntimeException("unary not unimplemented");
			}
			else if (ue.getOperator().equals(UnaryExpression.Operator.MINUS)) {
				throw new RuntimeException("unary minus");
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
				Register ptr = new Register(toLLVMObject(globalSymbolTable.get(id)));

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
					throw new RuntimeException("invalid binaryexpression type");
			}
		}

		else if (e instanceof InvocationExpression) {
			InvocationExpression ie = (InvocationExpression)e;
			ArrayList<Value> arguments = new ArrayList<Value>();
			for (Expression arg : ie.getArgs()) {
				Value argReg = buildExpression(arg, b);
				arguments.add(argReg);
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


		throw new RuntimeException ("buildexpression error: should not reach here " + e.toString());
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

		if (f.getType() instanceof IntType) {
				header += "i32";
		}
		else {
			header += "void";
		}

		header += " @" + f.getName() + "(";

		List<Declaration> params = f.getParams();

		for (int i = 0; i < params.size(); i++) {
			Declaration currDec = params.get(i);

			header += "i32 %" + currDec.getName();
			if (i != (params.size() - 1)) {
				header += ", ";
			}
		}

		header += ")";

		return header;
	}
}