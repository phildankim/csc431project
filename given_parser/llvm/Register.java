package llvm;

import java.util.*;

public class Register {

	public String reg;
	//public String type;
	private static int counter = 0;
	// needed for SSA
	// after done using for each function, maybe clear out so we don't run into any scope issues?
	private static HashMap<String, String> registers = new HashMap<String, String>();

	public Register(String reg) {
		this.reg = reg;
		//this.type = type;
	}

	public static void addToRegisters(String r, String s) {
		Register.registers.put(r, s);
	}

	public static boolean containsRegKey(String r) {
		return Register.registers.containsKey(r);
	}

	public static boolean containsRegVal(String s) {
		return Register.registers.containsValue(s);
	}

	public static String getRegVal(String s) {
		return Register.registers.get(s);
	}

	public static String getRegName() {
		return "%r" + Integer.toString(counter++);
	}

}