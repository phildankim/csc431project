package llvm;

import java.util.*;

public class Register {

	// needed for SSA
	private HashMap<String, String> registers = new HashMap<String, String>();

	private static int counter = 0;
	//private String value;
	//private String name;

	public static String getRegName() {
		return "%r" + Integer.toString(counter++);
	}

}