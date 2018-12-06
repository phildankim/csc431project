package llvm;

import llvm.*;
import java.util.*;

public class InstructionAlloca implements Instruction {

	public Value result;
	public LLVMObject type;

	public InstructionAlloca(LLVMObject type, Value res) {
		this.type = type;
		this.result = res;
	}


	@Override
	public String toString() {
		return this.result + " = alloca " + this.type.toString();
	}

	public ArrayList<Value> getRegisters() {
		ArrayList<Value> res = new ArrayList<>();
		res.add(result);

		return res;
	}
}