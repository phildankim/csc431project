package llvm;

import java.util.*;

public class InstructionPhi implements Instruction {
	
	public Value register;
	public String id;
	public Boolean invalid = false;

	public ArrayList<PhiOperand> phiOperands = new ArrayList<>();

	public InstructionPhi (Value register, String id) {
		this.register = register;
		this.id = id;
	}

	public void addOperand (String id, Value val) {
		PhiOperand po = new PhiOperand(id,val);
		phiOperands.add(po);
	}

	public ArrayList<Value> getRegisters() {
		ArrayList<Value> res = new ArrayList<>();

		for (PhiOperand po : this.phiOperands) {
			res.add(po.getValue());
		}

		res.add(register);
		return res;
	}

	public ArrayList<Value> getUses() {
		ArrayList<Value> res = new ArrayList<>();
		for (PhiOperand po : this.phiOperands) {
			res.add(po.getValue());
		}
		return res;
	}

	public Value getDef() {
		return register;
	}

	public String getId() {
		return id;
	}

	public Value getValue() {
		return register;
	}

	public void removeBadPhis() {
		for (PhiOperand phi : phiOperands) {
			if (phi.getValue().toString().equals(register.toString())) {
				phiOperands.remove(phi);
			}
		}
	}

	@Override
	public String toString() {

		// removeBadPhis();

		String returnString = register + " = phi " + register.getType();
		for (int i = 0; i < phiOperands.size(); i++) {
			PhiOperand po = phiOperands.get(i);
			returnString += " [" + po.getValue() + ", %" + po.getId() + "]";

			if (i < phiOperands.size() -1) {
				returnString += ", ";
			}

			// if (po.getValue().toString().equals(register.toString())) {
			//  	invalid = true;
			// }
		}

		if (invalid) {
			return "";
		}

		else {
			return returnString;
		}

	}
}