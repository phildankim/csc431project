package llvm;

import ast.*;
import cfg.*;
import llvm.*;

import java.util.*;
import java.io.BufferedWriter;
import java.io.IOException;

public class LLVM {

	private Program p;
	private ArrayList<CFG> cfgs = new ArrayList<CFG>();
	private ArrayList<Instruction> globalDecls = new ArrayList<Instruction>();
	private static HashMap<String, LLVMObject> globals = new HashMap<String, LLVMObject>();
	private List<TypeDeclaration> types;
	// key: name of struct, value: list of fields
	private static HashMap<String, ArrayList<LLVMObject>> structTable = new HashMap<String, ArrayList<LLVMObject>>();

	public LLVM(Program p) {
		this.p = p;
		setDeclInstructions();
		setTypeDeclInstructions();
		//LLVM.printGlobals();
		//LLVM.printStructTable();
		this.cfgs = CFGFactory.createAllCFG(p);
	}

	public static void printGlobals() {
		System.out.println("--Currently in LLVM.Globals");
		for (String key : LLVM.globals.keySet()) {
			System.out.println("Key: " + key + "\tValue: " + LLVM.getType(key));
		}
	}

	public static void addToGlobals(String s, LLVMObject t) {
		LLVM.globals.put(s, t);
	}

	public static LLVMObject getType(String id) {
		return LLVM.globals.get(id);
	}

	public void printProgram() {
		this.printTypes();
		for (CFG c : this.cfgs) {
			c.printCFG();
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

	public void printToARM(BufferedWriter writer) throws IOException {
		writer.write("\t.arch armv7-a\n");

		for (Instruction i : globalDecls) {
			if (i instanceof InstructionDecl) {
				writer.write ("/* hi i found a global declaration */\n");
			}
			else if (i instanceof InstructionTypeDecl) {
				writer.write("/* hi this is a struct declaration */\n");
			}
			else {
				throw new RuntimeException("globaldecls isnt an instruction decl wth! " + i.toString());
			}
		}

		for (CFG c: this.cfgs) {
			c.printToARM(writer);
		}

		writer.write(".PRINTLN_FMT:\n");
		writer.write("\t.asciz  \"%ld\\n\"\n");
		writer.write("\t.align. 2\n");
		writer.write(".PRINT_FMT:\n");
		writer.write("\t.asciz  \"%ld \"\n");
		writer.write("\t.align. 2\n");
		writer.write(".READ_FMT:\n");
		writer.write("\t.asciz  \"%ld\"\n");
		writer.write("\t.comm   .read_scratch,4,4\n");
		writer.write("\t.global __aeabi_idiv\n");
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

	public static void addStruct(String s, LLVMObject field) {
		ArrayList<LLVMObject> fieldsList = LLVM.structTable.get(s);

		if (fieldsList == null) {
			fieldsList = new ArrayList<LLVMObject>();
			fieldsList.add(field);
			LLVM.structTable.put(s, fieldsList);
		}
		else {
			if (!fieldsList.contains(field))
				fieldsList.add(field);
		}
	}

	public static LLVMObject getStructField(String structName, String fieldId) {
		ArrayList<LLVMObject> fields = LLVM.structTable.get(structName);

		for (LLVMObject field : fields) {
			if (field.getId().equals(fieldId)) {
				return field;
			}
		}
		return null;
	}

	public static int getFieldIndex(String structName, String fieldName) {
		ArrayList<LLVMObject> fields = LLVM.structTable.get(structName);
		LLVMObject field = LLVM.getStructField(structName, fieldName);
		return fields.indexOf(field);
	}

	public static void printStructTable() {
		System.out.println("--Currently in LLVM.structTable");
		Iterator it = LLVM.structTable.entrySet().iterator();
    	while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        System.out.println(pair.getKey() + " = " + pair.getValue());
    	}
	}
}