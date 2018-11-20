package llvm;

import java.util.*;

public class InstructionPhi implements Instruction {
	
	Value register;
	String id;
	Boolean invalid = false;

	ArrayList<PhiOperand> phiOperands = new ArrayList<>();

	public InstructionPhi (Value register, String id) {
		this.register = register;
		this.id = id;
	}

	public void addOperand (String id, Value val) {
		PhiOperand po = new PhiOperand(id,val);
		phiOperands.add(po);
	}

	public String getId() {
		return id;
	}

	public Value getValue() {
		return register;
	}

	@Override
	public String toString() {
		String returnString = register + " = phi " + register.getType();
		for (int i = 0; i < phiOperands.size(); i++) {
			PhiOperand po = phiOperands.get(i);
			returnString += " [" + po.getValue() + ", %" + po.getId() + "]";

			if (i < phiOperands.size() -1) {
				returnString += ", ";
			}

			if (po.getValue().toString().equals(register.toString())) {
			invalid = true;
			}
		}

		if (invalid) {
			return "";
		}

		else {
			return returnString;
		}

	}
}