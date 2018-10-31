package llvm;

import ast.*;
import cfg.*;

import java.util.*;
import java.io.BufferedWriter;
import java.io.IOException;

public class LLVM {

	private Program p;
	private ArrayList<CFG> cfgs = new ArrayList<CFG>();
	private ArrayList<Instruction> globalDecls = new ArrayList<Instruction>();

	// Key: struct name, Val: struct Object
	private static HashMap<String, LLVMObject> globalStructs = new HashMap<String, LLVMObject>();

	public LLVM(Program p) {
		this.p = p;
		setDeclInstructions();
		setTypeDeclInstructions();
		this.cfgs = CFGFactory.createAllCFG(p);
	}

	public void printProgram() {
		this.printTypes();
		for (CFG c : this.cfgs) {
			c.printCFG();
		}
	}

	public static void printStructs() {
		System.out.println("--Currently in LLVM.Structs");
		for (String key : globalStructs.keySet()) {
			StructObject struct = (StructObject)LLVM.getObj(key);
			System.out.println("Key: " + key + "\tValue: " + struct.toString());
			System.out.println("\tFields:");
			for (LLVMObject o : struct.getFields()) {
				System.out.println("\t\t" + o.toString() + " " + o.getId());
			}
		}
	}

	public void printInstructions(BufferedWriter writer) throws IOException {

		// header:
		writer.write("target triple=\"i686\"\n");

		for (Instruction i : globalDecls) {
			writer.write(i.toString() + "\n");
		}
		for (CFG c : this.cfgs) {
			c.printInstructions(writer);
		}

		// footer:
		writer.write("declare i8* @malloc(i32)\n");
		writer.write("declare void @free(i8*)\n");
		writer.write("declare i32 @printf(i8*, ...)\n");
		writer.write("declare i32 @scanf(i8*, ...)\n");
		writer.write("@.println = private unnamed_addr constant [5 x i8] c\"%ld\\0A\\00\", align 1\n");
		writer.write("@.print = private unnamed_addr constant [5 x i8] c\"%ld \\00\", align 1\n");
		writer.write("@.read = private unnamed_addr constant [4 x i8] c\"%ld\\00\", align 1\n");
		writer.write("@.read_scratch = common global i32 0, align 8\n");
	}

	public void setDeclInstructions() {
		for (Declaration d : this.p.getDecls()) {
	 		this.globalDecls.add(InstructionTranslator.setDeclInstruction(d));
		}
	}

	public void setTypeDeclInstructions() {
		for (TypeDeclaration td : this.p.getTypes()) {
			this.globalDecls.add(InstructionTranslator.setTypeDeclInstruction(td));
		}
	}

	public void printTypes() {
		for (Instruction i : this.globalDecls) {
			System.out.println(i.toString());
		}
	}

	public HashMap<String, LLVMObject> getHashMap() {
		return this.globalStructs;
	}

	public void clearStructs() {
		this.globalStructs.clear();
		System.out.println("Global Struct cleared");
	}

	public static void addToLocals(String s, LLVMObject o) {
		globalStructs.put(s, o);
	}

	public static LLVMObject getObj(String id) {
		return globalStructs.get(id);
	}
}