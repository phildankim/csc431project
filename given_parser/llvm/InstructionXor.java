package llvm;

import java.util.*;

public class InstructionXor implements Instruction {
	
	public Value operand1;
	public Value operand2;
	public Value register;

	public InstructionXor (Value register, Value operand1, Value operand2) {
		this.register = register;
		this.operand1 = operand1;
		this.operand2 = operand2;
	}

	@Override
	public String toString() {
		return register.toString() + " = xor " + operand1.getType() + " " + operand1 + ", " + operand2;
	}

	public ArrayList<Value> getRegisters() {
		ArrayList<Value> res = new ArrayList<>();
		res.add(operand1);
		res.add(operand2);
		res.add(register);
		return res;
	}
}