package llvm;

import java.util.*;
import ast.*;

public class Register implements Value {

	public String name;
	public LLVMObject type;
	private static int counter = 0;
	// needed for SSA
	// after done using for each function, maybe clear out so we don't run into any scope issues?
	// key: Register number (String), val: Register
	private static HashMap<String, Register> registers = new HashMap<String, Register>();

	public Register(LLVMObject type) {
		this.name = "%u" + counter;
		counter = counter + 1;
		this.type = type;

		addToRegisters(name, this);
	}

	public Register(LLVMObject type, String name) {
		this.name = "%" + name;
		this.type = type;
		addToRegisters(name, this);
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

	public String getName() {
		return name;
	}

	public LLVMObject getType() {
		return this.type;
	}

	public static void printRegisters() {
		System.out.println("--Currently in Registers");
		for (String key : Register.registers.keySet()) {
			System.out.println("Key: " + key + "\tValue: " + Register.getReg(key));
		}
	}

	public String toString() {
		return name;
	}

	public void printRegister() {
		System.out.println("Register " + name + " contains " + this.getType());
	}
}