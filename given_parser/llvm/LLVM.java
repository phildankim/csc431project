package llvm;

import ast.*;
import cfg.*;

import java.util.*;

public class LLVM {

	private Program p;
	private ArrayList<CFG> cfgs = new ArrayList<CFG>();
	private ArrayList<Instruction> globalDecls = new ArrayList<Instruction>();

	public LLVM(Program p) {
		this.p = p;
		this.cfgs = CFGFactory.createAllCFG(p);
		//setDeclInstructions();
		setTypeDeclInstructions();
	}

	public void printProgram() {
		this.printTypes();
		for (CFG c : this.cfgs) {
			c.printCFG();
		}
	}

	// public void setDeclInstructions() {
	// 	for (Declaration d : this.p.getDecls()) {
	// 		this.globalDecls.add(InstructionTranslator.setDeclInstruction(d));
	// 	}
	// }

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
}