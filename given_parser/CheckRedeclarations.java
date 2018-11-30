// redeclaration checker

import ast.*;
import java.util.*;


public class CheckRedeclarations {

	public static void checkProgram(Program program) {
		//System.out.println ("Checking redeclarations.");

		//Check global variables:
		List<Declaration> decls = program.getDecls();
		HashSet<String> globalNames = new HashSet<String>();
		for (Declaration decl : decls) {
			if (!globalNames.add(decl.getName())) {
				System.out.println("ERROR on Line " + decl.getLine() + ": global variable " + decl.getName() + " already defined.");
			}
		}

		//Check structs including fields:
		List<TypeDeclaration> structs = program.getTypes();
		HashSet<String> structNames = new HashSet<String>();
		for (TypeDeclaration struct : structs) {
			if (!structNames.add(struct.getName())){
				System.out.println("ERROR on Line " + struct.getLine() + ": struct " + struct.getName() + " already defined.");
			}

			HashSet<String> fieldNames = new HashSet<String>();
			for (Declaration field : struct.getFields()) {
				if (!fieldNames.add(field.getName())) {
					System.out.println("ERROR on Line " + field.getLine() + ": struct: " + struct.getName() + " field " + field.getName() + " already defined.");
				}
			}
		}

		//Check functions incuding params and locals:
		List<Function> funcs = program.getFuncs();
		HashSet<String> funcNames = new HashSet<String>();
		for (Function func : funcs) {
			if (!funcNames.add(func.getName())) {
				System.out.println("ERROR on Line " + func.getLine() + ": function " + func.getName() + " already defined");
			}

			// check function params:
			List<Declaration> params = func.getParams();
			HashSet<String> paramNames = new HashSet<String>();
			for (Declaration param : params) {
				if (!paramNames.add(param.getName())) {
					System.out.println("ERROR on Line " + param.getLine() + ": function: " + func.getName() + " parameter " + param.getName() + " already defined.");
				}
			}

			// check function locals and locals with parameters:
			List<Declaration> locals = func.getLocals();
			HashSet<String> localNames = new HashSet<String>();
			for (Declaration local : locals) {
				if (!localNames.add(local.getName())) {
					System.out.println("ERROR on Line " + local.getLine() + ": function: " + func.getName() + " local variable " + local.getName() + " already defined.");
				}

				if (!paramNames.add(local.getName())) {
					System.out.println("ERROR on Line " + local.getLine() + ": function: " + func.getName() + " local variable " + local.getName() + " already defined as parameter.");
				}
			}
		}

		//Check for main function : needs to be present, with 0 args and return IntType.

		Boolean foundMain = false;
		for (Function func: funcs) {
			if (func.getName().equals("main")) {
				foundMain = true;

				if (!func.getType().toString().equals("i32")) {
					System.out.println("ERROR: main function return type is " + func.getType());
				}

				if (func.getParams().size() > 0) {
					System.out.println("ERROR on Line " + func.getLine() + ": main function cannot have any arguments");
				}
			}
		}

		if (!foundMain) {
			System.out.println("ERROR: function main not found.");
		}
	}
}