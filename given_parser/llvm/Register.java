package llvm;

import java.util.*;

public class Register {

	public String num;
	public LLVMObject type;
	private static int counter = 0;
	// needed for SSA
	// after done using for each function, maybe clear out so we don't run into any scope issues?
	// key: Register, val: String identifier associated with register
	private static HashMap<Register, String> registers = new HashMap<Register, String>();

	public Register(String num, LLVMObject type) {
		this.num = num;
		this.type = type;
	}

	public static void addToRegisters(Register r, String s) {
		Register.registers.put(r, s);
	}

	public static boolean containsRegKey(Register r) {
		return Register.registers.containsKey(r);
	}

	public static boolean containsRegVal(String s) {
		return Register.registers.containsValue(s);
	}

	public static String getRegVal(Register r) {
		return Register.registers.get(r);
	}

	public static String getRegName() {
		return "%r" + Integer.toString(counter++);
	}

}