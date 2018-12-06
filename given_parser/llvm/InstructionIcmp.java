package llvm;

import java.util.*;

public class InstructionIcmp implements Instruction 
{
	public String condition;
	public Value operand1;
	public Value operand2;
	public Value result;

	public InstructionIcmp (Value result, String condition, Value operand1, Value operand2) {
		this.result = result;
		this.condition = condition;
		this.operand1 = operand1;
		this.operand2 = operand2;
	}

	public Value getResult() {
		return this.result;
	}

	@Override
	public String toString() {
		return result + " = icmp " + condition + " " + operand1.getType() + " " + operand1 + ", " + operand2;
	}

	public ArrayList<Value> getRegisters() {
		ArrayList<Value> res = new ArrayList<>();
		res.add(operand1);
		res.add(operand2);
		res.add(result);
		return res;
	}

	public ArrayList<Value> getUses() {
		ArrayList<Value> res = new ArrayList<>();
		res.add(operand1);
		res.add(operand2);
		return res;
	}

	public Value getDef() {
		return result;
	}

}