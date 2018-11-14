package llvm;

import llvm.*;
import java.util.*;

public class InstructionAlloca implements Instruction {

	Value result;
	LLVMObject type;

	public InstructionAlloca(LLVMObject type, Value res) {
		this.type = type;
		this.result = res;
	}


	@Override
	public String toString() {
		return this.result + " = alloca " + this.type.toString();
	}
}