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
	private List<TypeDeclaration> types;

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

	public static TypeDeclaration getStruct(Program p, String struct) {
		for (TypeDeclaration td : p.getTypes()) {
			if (td.getName().equals(struct)) {
				return td;
			}
		}
		return null;
	}

	public static int getFieldIndex(Program p, String struct, String field) {
		TypeDeclaration td = LLVM.getStruct(p, struct);
		int counter = 0;
		for (Declaration d : td.getFields()) {
			if (d.getName().equals(field)) {
				return counter;
			}
			counter++;
		}
		return -1;
	}
}