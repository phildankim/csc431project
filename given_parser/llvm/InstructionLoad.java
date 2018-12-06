package llvm;

import llvm.*;
import java.util.*;

public class InstructionLoad implements Instruction{
	
	public Value result;
	public Value pointer;
	public LLVMObject type;

	public InstructionLoad (Value result, Value pointer, LLVMObject type) {
		this.result = result;
		this.pointer = pointer;
		this.type = type;
	}

	@Override
	public String toString() {
		return result.toString() + " = load " + type + "* " + pointer; 
	}

	public ArrayList<Value> getRegisters() {
		ArrayList<Value> res = new ArrayList<>();
		res.add(result);
		res.add(pointer);
		return res;
	}
}