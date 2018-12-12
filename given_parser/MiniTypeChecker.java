import java.util.*;
import ast.*;

public class MiniTypeChecker {

	public static void checkProgram (Program program) throws TypeCheckException
	{

		// System.out.println ("\n\nChecking program.");

		HashMap <String,Type> symbolTable = new HashMap<String,Type>();
		HashMap <String, List <Declaration>> funcParamsTable = new HashMap <String, List<Declaration>>();
		HashMap <String, List <Declaration>> structTable = new HashMap <String, List<Declaration>>();

		// add global declarations to symbol table
		for (Declaration decl : program.getDecls()) {
			symbolTable.put (decl.getName(), decl.getType());
		}

		// add function names with return types to symbol table
		for (Function func : program.getFuncs()) {
			symbolTable.put (func.getName(), func.getType());
		}

		// table for function formal parameters
		for (Function func: program.getFuncs()) {
			funcParamsTable.put(func.getName(),func.getParams());
		}

		// struct table
		for (TypeDeclaration struct : program.getTypes()) {
			structTable.put(struct.getName(), struct.getFields());
		}

		// displayData(symbolTable,funcParamsTable,structTable);

		// check functions:
		for (Function func : program.getFuncs()) {
			HashMap <String, Type> funcSymbolTable = new HashMap<String,Type>();
			funcSymbolTable.putAll(symbolTable); // add global symbol table

			for (Declaration param : func.getParams()) {
				funcSymbolTable.put(param.getName(), param.getType());
			}

			for (Declaration local : func.getLocals()) {
				funcSymbolTable.put(local.getName(), local.getType());
			}

			checkFunction (func, funcSymbolTable, funcParamsTable, structTable);
		}
	}

	public static void checkFunction (Function func,
									  HashMap <String, Type> symbolTable,
									  HashMap <String, List<Declaration>> funcParamsTable,
									  HashMap <String, List<Declaration>> structTable) 
								      throws TypeCheckException{
		BlockStatement funcBody = (BlockStatement)func.getBody();						      	
      	if (funcBody.getStatements().isEmpty()  && !(func.getType() instanceof VoidType)) {
      		throw new TypeCheckException("Function " + func.getName() + " does not return anything. Expected return type of " + func.getType());
      	}
      	if (!MiniTypeChecker.checkForReturn(funcBody) && !(func.getType() instanceof VoidType)) {
      		throw new TypeCheckException("Function " + func.getName() + " does not return along all paths.");
      	}
		Type funcReturnType = func.getType();
		Type bodyReturnType = checkStatement(func.getBody(), symbolTable, funcParamsTable, structTable, funcReturnType);


		
		// if (!funcReturnType.toString().equals(bodyReturnType.toString())) {
		// 	throw new TypeCheckException ("ERROR on Line " + func.getLine() + ": invalid return type. Expected " + funcReturnType.toString() +" but got " + bodyReturnType.toString());
		// }
	
	}

	// returns true if the statement contains a return
	public static boolean checkForReturn(Statement statement) {
		if (statement instanceof BlockStatement) {
			boolean ret = false;
			for (Statement s : ((BlockStatement)statement).getStatements()) {
				ret = MiniTypeChecker.checkForReturn(s);
			}
			return ret;
		}
		else if (statement instanceof ConditionalStatement) {
			Statement thenBlock = ((ConditionalStatement)statement).getThen();
			Statement elseBlock = ((ConditionalStatement)statement).getElse();

			boolean then = MiniTypeChecker.checkForReturn(thenBlock);
			boolean els = MiniTypeChecker.checkForReturn(elseBlock);

			if (then && els) {
				return true;
			}
			return false;
		}
		else if (statement instanceof ReturnStatement || statement instanceof ReturnEmptyStatement) {
			return true;
		}
		else {
			return false;
		}
	}

	public static Type checkStatement (Statement statement,
								  	   HashMap <String, Type> symbolTable,
								  	   HashMap <String, List<Declaration>> funcParamsTable,
								  	   HashMap <String, List<Declaration>> structTable,
								  	   Type expectedReturnType) 
									   throws TypeCheckException{

		if (statement instanceof BlockStatement) {
			for (Statement s : ((BlockStatement)statement).getStatements()) {
				Type type = checkStatement (s, symbolTable,funcParamsTable, structTable, expectedReturnType);
			}

			return new VoidType();
		}

		else if (statement instanceof AssignmentStatement) {
			Type lValue = checkLValue(((AssignmentStatement)statement).getTarget(), symbolTable, funcParamsTable, structTable,expectedReturnType);
			Type rValue = checkExpression(((AssignmentStatement)statement).getSource(), symbolTable, funcParamsTable, structTable,expectedReturnType);

			if (!(rValue instanceof NullType) && !lValue.toString().equals(rValue.toString())){
				throw new TypeCheckException("ERROR on Line " + ((AssignmentStatement)statement).getLine() + ": invalid assignment. target = " + lValue.toString() + ", source = " + rValue.toString());
			}

			return lValue;
		}

		else if (statement instanceof PrintStatement) {
			Type type = checkExpression (((PrintStatement)statement).getExpression(), symbolTable, funcParamsTable, structTable, expectedReturnType);
			if (!(type instanceof IntType)) {
				throw new TypeCheckException("ERROR on Line " + ((PrintStatement)statement).getLine() + ": print needs IntType, found " + type.toString());
			}

			return type;
		}

		else if (statement instanceof PrintLnStatement) {
			Type type = checkExpression (((PrintLnStatement)statement).getExpression(), symbolTable, funcParamsTable, structTable, expectedReturnType);
			if (!(type instanceof IntType)) {
				throw new TypeCheckException("ERROR on Line " + ((PrintLnStatement)statement).getLine() + ": printline needs IntType, found " + type.toString());
			}

			return type;
		}

		else if (statement instanceof ConditionalStatement) {
			Statement thenBlock = ((ConditionalStatement)statement).getThen();
			Statement elseBlock = ((ConditionalStatement)statement).getElse();
			// boolean thenRet = MiniTypeChecker.checkForReturn(thenBlock);
			// boolean elseRet = MiniTypeChecker.checkForReturn(elseBlock);

			// if (thenRet != elseRet) {
			// 	throw new TypeCheckException("Function does not return along all paths.");
			//}

			Type guard = checkExpression (((ConditionalStatement)statement).getGuard(), symbolTable, funcParamsTable, structTable, expectedReturnType);
			Type then = checkStatement (((ConditionalStatement)statement).getThen(), symbolTable, funcParamsTable, structTable, expectedReturnType);
			Type els = checkStatement (((ConditionalStatement)statement).getElse(), symbolTable, funcParamsTable, structTable, expectedReturnType);
			

			
			if (!(guard instanceof BoolType)) {
				throw new TypeCheckException("ERROR on Line " + ((ConditionalStatement)statement).getLine() + ": guard needs to be BoolType, not " + guard.toString());
			}

			if (!then.toString().equals(els.toString())) {
				throw new TypeCheckException("ERROR on Line " + ((ConditionalStatement)statement).getLine() + ": different return types, then is " + then.toString() + ", else is " + els.toString());
			}

			return then;
		}
		else if (statement instanceof WhileStatement) {
			Type guard = checkExpression (((WhileStatement)statement).getGuard(), symbolTable, funcParamsTable, structTable, expectedReturnType);
			Type body = checkStatement (((WhileStatement)statement).getBody(), symbolTable, funcParamsTable, structTable, expectedReturnType);
			if (!(guard instanceof BoolType)) {
				throw new TypeCheckException("ERROR on Line " + ((WhileStatement)statement).getLine() + ": guard needs to be BoolType, not " + guard.toString());
			}

			return body;
		}
		else if (statement instanceof DeleteStatement) {
			Type type = checkExpression (((DeleteStatement)statement).getExpression(), symbolTable, funcParamsTable, structTable, expectedReturnType);
			if (!(type instanceof StructType)) {
				throw new TypeCheckException ("ERROR on Line " + ((DeleteStatement)statement).getLine() + ": delete statement needs to be StructType, not " + type.toString());
			}
		}
		else if (statement instanceof ReturnStatement) {
			Type checkThis =  checkExpression(((ReturnStatement)statement).getExpression(), symbolTable, funcParamsTable, structTable, expectedReturnType);
			if (!checkThis.toString().equals(expectedReturnType.toString()) && !(checkThis instanceof NullType)) {
				throw new TypeCheckException ("ERROR :on Line " + ((ReturnStatement)statement).getLine() +": return statement type needs to be " + expectedReturnType.toString() + " but it's " + checkThis.toString());
			}

			return checkThis;
		}
		else if (statement instanceof InvocationStatement) {
			Type type = checkExpression (((InvocationStatement)statement).getExpression(), symbolTable, funcParamsTable, structTable, expectedReturnType);
			return type;
		}
		else if (statement instanceof ReturnEmptyStatement) {
			if (expectedReturnType instanceof VoidType) {
				return new VoidType();
			}
			throw new TypeCheckException ("ERROR :on Line " + ((ReturnEmptyStatement)statement).getLine() +": return statement type needs to be " + expectedReturnType.toString() + " but it's empty" );
		}

		// PLACEHOLDER
		// throw new TypeCheckException ("statement type error. " + statement.toString());

		// ENED TO IMPLEMENT DELETE STATEMENT
		return new VoidType();
	}

	public static Type checkLValue(Lvalue lValue,
									HashMap <String, Type> symbolTable,
									HashMap <String, List<Declaration>> funcParamsTable,
									HashMap <String, List<Declaration>> structTable,
									Type expectedReturnType) 
									throws TypeCheckException {

		if (lValue instanceof LvalueId) {
			String id = ((LvalueId)lValue).getId();
			if (symbolTable.containsKey(id)) {
				return symbolTable.get(id);
			}
			else {
				throw new TypeCheckException("ERROR on Line " + ((LvalueId)lValue).getLine() + ": Lvalid id " + id + " does not exist.");
			}
		}

		else if (lValue instanceof LvalueDot) {
			Type left = checkExpression (((LvalueDot)lValue).getLeft(), symbolTable, funcParamsTable, structTable, expectedReturnType);

			if (left instanceof StructType) {
				if (structTable.containsKey(((StructType)left).getName())) {
					List<Declaration> structFields = structTable.get(((StructType)left).getName());
					String id = ((LvalueDot)lValue).getId();

					for (Declaration field : structFields) {
						if (field.getName().equals(id)) {
							return field.getType();
						}
					}

					throw new TypeCheckException("ERROR on Line" + ((LvalueDot)lValue).getLine() + ": missing field " + id);
				}

				else {
					throw new TypeCheckException("ERROR on Line" + ((LvalueDot)lValue).getLine() + ": invalid struct " + ((StructType)left).getName());
				}
			}
			else {
				throw new TypeCheckException("ERROR on Line " + ((LvalueDot)lValue).getLine() + ": LValueDot needs to be a StructType, instead found " + left.toString());
			}
		}

		throw new TypeCheckException("Invalid LValue");
	}

	public static Type checkExpression(Expression exp,
									   HashMap <String, Type> symbolTable,
									   HashMap <String, List<Declaration>> funcParamsTable,
									   HashMap <String, List<Declaration>> structTable,
									   Type expectedReturnType) 
									   throws TypeCheckException {

		if (exp instanceof TrueExpression) {
			return new BoolType();
		}
		else if (exp instanceof FalseExpression) {
			return new BoolType();
		}
		else if (exp instanceof IntegerExpression) {
			return new IntType();
		}

		else if (exp instanceof DotExpression) {
			Type left = checkExpression (((DotExpression)exp).getLeft(), symbolTable, funcParamsTable, structTable, expectedReturnType);

			if (left instanceof StructType) {
				if (structTable.containsKey(((StructType)left).getName())) {
					List<Declaration> structFields = structTable.get(((StructType)left).getName());
					String id = ((DotExpression)exp).getId();

					for (Declaration field : structFields) {
						if (field.getName().equals(id)) {
							return field.getType();
						}
					}

					throw new TypeCheckException("ERROR on Line" + ((DotExpression)exp).getLine() + ": dot missing field " + id);
				}

				else {
					throw new TypeCheckException("ERROR on Line" + ((DotExpression)exp).getLine() + ": invalid struct on dot" + ((StructType)left).getName());
				}
			}
			else {
				throw new TypeCheckException("ERROR on Line " + ((DotExpression)exp).getLine() + ": dot expression needs to be a StructType, instead found " + left.toString());
			}
		}

		else if (exp instanceof IdentifierExpression) {
			if (symbolTable.containsKey( ((IdentifierExpression)exp).getId() )){
				return symbolTable.get(((IdentifierExpression)exp).getId());
			}
			else {
				throw new TypeCheckException ("ERROR on Line " + ((IdentifierExpression)exp).getLine() + ": identifier does not exist: " + ((IdentifierExpression)exp).getId());
			}
		}

		else if (exp instanceof BinaryExpression) {

			Type left = checkExpression(((BinaryExpression)exp).getLeft(), symbolTable, funcParamsTable, structTable, expectedReturnType);
			Type right = checkExpression(((BinaryExpression)exp).getRight(), symbolTable, funcParamsTable, structTable, expectedReturnType);

			switch (((BinaryExpression)exp).getOperator()) {
				case PLUS:
				case MINUS:
				case DIVIDE:
				case TIMES:
					if (!(left instanceof IntType)) {
						throw new TypeCheckException("ERROR on Line " + ((BinaryExpression)exp).getLine() + ": left should be IntType but instead it's " + left.toString() );
					}
					if (!(right instanceof IntType)) {
						throw new TypeCheckException("ERROR on Line " + ((BinaryExpression)exp).getLine() + ": right should be IntType but instead it's " + right.toString() );
					}
					return new IntType();
				case LT:
				case GT:
				case GE:
				case LE:
					if (!(left instanceof IntType)) {
						throw new TypeCheckException("ERROR on Line " + ((BinaryExpression)exp).getLine() + ": left should be IntType but instead it's " + left.toString() );
					}
					if (!(right instanceof IntType)) {
						throw new TypeCheckException("ERROR on Line " + ((BinaryExpression)exp).getLine() + ": right should be IntType but instead it's " + right.toString() );
					}
					return new BoolType();

				case EQ:
				case NE:
					if (left instanceof IntType && (right instanceof IntType || right instanceof NullType)) {
						return new BoolType();
					}
					if (left instanceof StructType && (right instanceof StructType || right instanceof NullType)) {
						return new BoolType();
					}

					else throw new TypeCheckException ("ERROR on Line " + ((BinaryExpression)exp).getLine() + ": eq/ne expressions different. left = " + left.toString() + ", right = " + right.toString());

				case AND:
				case OR:
					if (!(left instanceof BoolType)) {
						throw new TypeCheckException("ERROR on Line " + ((BinaryExpression)exp).getLine() + ": binaryAND/OR left should be BoolType but instead it's " + left.toString() );
					}
					if (!(right instanceof BoolType)) {
						throw new TypeCheckException("ERROR on Line " + ((BinaryExpression)exp).getLine() + ": binaryAND/OR right should be BoolType but instead it's " + right.toString() );
					}
					return new BoolType();

				default:
					throw new TypeCheckException ("invalid binexp operand");
			}
		}

		else if (exp instanceof InvocationExpression) {
			if (symbolTable.containsKey(((InvocationExpression)exp).getName())) {
				
				ArrayList<Type> args = new ArrayList<Type>();
				ArrayList<Type> params = new ArrayList<Type>();

				for (Expression expr : ((InvocationExpression)exp).getArgs()) {
					args.add(checkExpression(expr, symbolTable, funcParamsTable, structTable, expectedReturnType));
				}

				List<Declaration> decls = funcParamsTable.get(((InvocationExpression)exp).getName());
				for (Declaration decl : decls) {
					params.add(decl.getType());
				}

				if (args.size() != params.size()) {
					throw new TypeCheckException("ERROR on Line " + ((InvocationExpression)exp).getLine() + ": number of args and params dont match"); 
				}

				for (int i = 0; i < args.size(); i++) {
					if (!args.get(i).toString().equals(params.get(i).toString())) {
						if (!args.get(i).toString().equals("null") && !(params.get(i) instanceof StructType)) {
							throw new TypeCheckException ("ERROR : arg " + i +" type does not match param type, arg is " + args.get(i).toString() + " while param is " + params.get(i).toString());
						}
					}
				}

				return symbolTable.get(((InvocationExpression)exp).getName());
			}
			else {
				throw new TypeCheckException("ERROR on Line " + ((InvocationExpression)exp).getLine() + ": cant find function " + ((InvocationExpression)exp).getName()); 
			}
		}

		else if (exp instanceof UnaryExpression) {
			Type operand = checkExpression (((UnaryExpression)exp).getOperand(),symbolTable, funcParamsTable, structTable,expectedReturnType );

			if ( ((UnaryExpression)exp).getOperator().equals(UnaryExpression.Operator.NOT) ) {
				if (operand instanceof BoolType) {
					return new BoolType();
				}
				else {
					throw new TypeCheckException ("ERROR on Line " + ((UnaryExpression)exp).getLine() + ": not needs a booltype, found a " + operand.toString());
				}
			}

			else if ( ((UnaryExpression)exp).getOperator().equals(UnaryExpression.Operator.MINUS) ) {
				if (operand instanceof IntType) {
					return new IntType();
				}
				else {
					throw new TypeCheckException ("ERROR on Line " + ((UnaryExpression)exp).getLine() + ": minus needs an inttype, found a " + operand.toString());
				}
			}
		}

		else if (exp instanceof NullExpression) {
			return new NullType();
		}

		else if (exp instanceof NewExpression) {
			if (structTable.containsKey(((NewExpression)exp).getId())) {
				return new StructType (((NewExpression)exp).getLine(), ((NewExpression)exp).getId());
			}
			else throw new TypeCheckException ("ERROR on Line " + ((NewExpression)exp).getLine() + ": did not find struct");
		}

		else if (exp instanceof ReadExpression) {
			return new IntType();
		}

		//return new VoidType();
		// need to implement readexpression
		throw new TypeCheckException("Need to implement checkExpression for " + exp.toString());
 
	}
	public static void displayData(HashMap <String, Type> symbolTable,
							  HashMap <String, List<Declaration>> funcParamsTable,
						      HashMap <String, List<Declaration>> structTable) {
 		// DATA DUMP : 
		System.out.println ("\nSymbol table: ");

		for (String name : symbolTable.keySet()) {
			Type x = symbolTable.get(name);
			System.out.println (name  + " : " + x);
		}

		System.out.println ("\nFunction params table:");

		for (String name: funcParamsTable.keySet()) {
			List<Declaration> params = funcParamsTable.get(name);

			System.out.println("fun " + name);

			for (Declaration param : params) {
				System.out.println ("    "  + param.getName() + " : " + param.getType());
			}
		}

		System.out.println("\nStruct Table");
		for (String name: structTable.keySet()) {
			List<Declaration> fields = structTable.get(name);

			System.out.println("struct " + name);

			for (Declaration field: fields) {
				System.out.println ("    " + field.getName() + " : " + field.getType());
			}
		}
	}
}