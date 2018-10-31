package llvm;

import ast.*;
import java.util.*;

public class InstructionAlloca implements Instruction {

	String result;
	Object type;

	public InstructionAlloca(LLVMObject obj, String res) {
		this.type = obj;
		this.result = "%" + res;
	}


	@Override
	public String toString() {
		return this.result + " = alloca " + this.type.toString();
	}
}