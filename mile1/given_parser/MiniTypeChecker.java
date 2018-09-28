import java.util.*;
import ast.*;

public class MiniTypeChecker {

	public static void checkProgram (Program program) {

		System.out.println ("\n\nChecking program.");

		HashMap <String,Type> symbolTable = new HashMap<String,Type>();

		for (Declaration decl : program.getDecls()) {
			symbolTable.put (decl.getName(), decl.getType());
		}

		System.out.println ("Symbol table: ");

		for (String name : symbolTable.keySet()) {
			Type x = symbolTable.get(name);
			System.out.println (name  + " is type " + x);
		}
	}
}