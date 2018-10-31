package llvm;

import java.util.*;

public class Register {

	public String num;
	public LLVMObject type;
	public String id;
	private static int counter = 0;
	// needed for SSA
	// after done using for each function, maybe clear out so we don't run into any scope issues?
	// key: Register number (String), val: Register
	private static HashMap<String, Register> registers = new HashMap<String, Register>();

	public Register(String num, LLVMObject type, String id) {
		this.num = num;
		this.type = type;
		this.id = id;
	}

	public static void addToRegisters(String s, Register reg) {
		Register.registers.put(s, reg);
	}

	public static boolean containsRegKey(String r) {
		return Register.registers.containsKey(r);
	}

	public static boolean containsRegVal(Register s) {
		return Register.registers.containsValue(s);
	}

	public static Register getReg(String r) {
		return Register.registers.get(r);
	}

	public static String getNewRegNum() {
		return "%r" + Integer.toString(counter++);
	}

	public String getRegNum() {
		return this.num;
	}

	public static void printRegisters() {
		System.out.println("--Currently in Registers");
		for (String key : Register.registers.keySet()) {
			System.out.println("Key: " + key + "\tValue: " + Register.getReg(key));
		}
	}

	public String toString() {
		return "Register " + this.getRegNum() + " contains object " + this.type + " with id " + this.id;  
	}
}