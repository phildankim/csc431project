package llvm;

import llvm.*;
import java.util.*;

public class InstructionAlloca implements Instruction {

	String result;
	LLVMObject type;

	public InstructionAlloca(LLVMObject type, String res) {
		this.type = type;
		this.result = res;
	}


	@Override
	public String toString() {
		return "%" + this.result + " = alloca " + this.type.toString();
	}
}