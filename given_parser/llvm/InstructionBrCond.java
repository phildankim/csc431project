package llvm;

import java.util.*;

public class InstructionBrCond implements Instruction{
	

	public Value condition;
	public String labelTrue;
	public String labelFalse;

	public InstructionBrCond (Value condition, String labelTrue, String labelFalse) {
		this.condition = condition;
		this.labelTrue = labelTrue;
		this.labelFalse = labelFalse;
	}

	@Override
	public String toString() {
		return "br i1 " + condition + ", label %" + labelTrue + ", label %" + labelFalse; 
	}

	public ArrayList<Value> getRegisters() {
		ArrayList<Value> res = new ArrayList<>();
		res.add(condition);

		return res;
	}

	public ArrayList<Value> getUses() {
		ArrayList<Value> res = new ArrayList<>();
		res.add(condition);
		return res;
	}

	public Value getDef() {
		return new NullValue();
	}
}