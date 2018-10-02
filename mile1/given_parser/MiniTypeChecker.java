import java.util.*;
import ast.*;

public class MiniTypeChecker {

	public static void checkProgram (Program program) throws TypeCheckException
	{

		System.out.println ("\n\nChecking program.");

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

		displayData(symbolTable,funcParamsTable,structTable);

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
		Type funcReturnType = func.getType();
		Type bodyReturnType = checkStatement(func.getBody(), symbolTable, funcParamsTable, structTable);

		if (!funcReturnType.toString().equals(bodyReturnType.toString())) {
			throw new TypeCheckException ("ERROR on Line " + func.getLine() + ": invalid return type. Expected " + funcReturnType.toString() +" but got " + bodyReturnType.toString());
		}
	}

	public static Type checkStatement (Statement statement,
								  	   HashMap <String, Type> symbolTable,
								  	   HashMap <String, List<Declaration>> funcParamsTable,
								  	   HashMap <String, List<Declaration>> structTable) 
									   throws TypeCheckException{
		if (statement instanceof BlockStatement) {
			for (Statement s : ((BlockStatement)statement).getStatements()) {
				Type type = checkStatement (s, symbolTable,funcParamsTable, structTable);
				return type;
			}
		}

		else if (statement instanceof AssignmentStatement) {
			Type lValue = checkLValue(((AssignmentStatement)statement).getTarget(), symbolTable, funcParamsTable, structTable);
		}

		// PLACEHOLDER
		return new IntType();
	}

	public static Type checkLValue(Lvalue lValue,
									HashMap <String, Type> symbolTable,
									HashMap <String, List<Declaration>> funcParamsTable,
									HashMap <String, List<Declaration>> structTable) 
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
			Type left = checkExpression (((LvalueDot)lValue).getLeft(), symbolTable, funcParamsTable, structTable);

			if (left instanceof StructType) {
				if (structTable.containsKey(((StructType)left).getName())) {
					List<Declaration> structFields = structTable.get(((StructType)left).getName());
					String id = ((LvalueDot)lValue).getId();

					for (Declaration field : structFields) {
						if (field.getName().equals(id)) {
							return field.getType();
						}
					}
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
									   HashMap <String, List<Declaration>> structTable) 
									   throws TypeCheckException {



		throw new TypeCheckException("Need to implement checkExpression");
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