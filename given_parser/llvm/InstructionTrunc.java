package llvm;

import java.util.*;

public class InstructionTrunc implements Instruction {
	
	public Value result;
	public Value register;

	public InstructionTrunc (Value result, Value register) {
		this.result = result;
		this.register = register;

	}

	@Override
	public String toString() {
		return result + " = trunc " + register.getType() + " " + register + " to i1";
	}

	public ArrayList<Value> getRegisters() {
		ArrayList<Value> res = new ArrayList<>();
		res.add(result);
		res.add(register);
		return res;
	}

	public ArrayList<Value> getUses() {
		ArrayList<Value> res = new ArrayList<>();
		res.add(register);
		return res;
	}

	public Value getDef() {
		return result;
	}
}