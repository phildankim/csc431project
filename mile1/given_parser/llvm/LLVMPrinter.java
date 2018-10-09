package llvm;

import java.util.*;
import ast.*;

public class LLVMPrinter {

	private static String targetFilename = "";

	public static void print(Program program, String filename) {

		targetFilename = filename.substring(0, filename.length()-4) + "ll";
		System.out.println(targetFilename);

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

			processFunction (func, funcSymbolTable, funcParamsTable, structTable);
		}
	}

	public static void processFunction (Function func,
									  HashMap <String, Type> symbolTable,
									  HashMap <String, List<Declaration>> funcParamsTable,
									  HashMap <String, List<Declaration>> structTable) {

		System.out.println("yee");
	}
}