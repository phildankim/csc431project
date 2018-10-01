import java.util.*;
import ast.*;

public class MiniTypeChecker {

	public static void checkProgram (Program program) {

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