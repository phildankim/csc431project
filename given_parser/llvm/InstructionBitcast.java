package llvm;

import java.util.*;

public class InstructionBitcast implements Instruction {
	
	public Value result;
	public Value register;
	public String structName;
	public Boolean isMalloc;

	public InstructionBitcast (Value result, Value register, String structName, Boolean isMalloc) {
		this.result = result;
		this.register = register;
		this.structName = structName;
		this.isMalloc = isMalloc;
	}

	@Override
	public String toString() {

		if (isMalloc) {
			return result + " = bitcast i8* " + register + " to %struct." + structName + "*";
		}
		else {
			return result + " = bitcast %struct." + structName + "* " + register + " to i8*";
		}
	}

	public ArrayList<Value> getRegisters() {
		ArrayList<Value> res = new ArrayList<>();
		res.add(result);
		res.add(register);
		return res;
	}
}