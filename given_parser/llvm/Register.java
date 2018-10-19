package llvm;

import java.util.*;

public class Register {

	private static int counter = 0;
	//private String value;
	//private String name;

	public static String getRegName() {
		return "%r" + Integer.toString(counter++);
	}

}