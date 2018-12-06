package llvm;

import cfg.*;
import java.util.*;

public class InstructionStore implements Instruction {

	public Value value; //target
	public Value pointer; //source
	public LLVMObject type;

	public InstructionStore (Value target, Value source) {
		this.value = source;
		this.pointer = target;
		this.type = target.getType();
	}

	public InstructionStore (Value target, Value source, LLVMObject type) {
		this.value = source;
		this.type = type;
		this.pointer = target;

	}

	@Override
	public String toString() {
		return "store " + type + " " + value + ", " + type +"* " + pointer;
	}

	public ArrayList<Value> getRegisters() {
		ArrayList<Value> res = new ArrayList<>();
		res.add(value);
		res.add(pointer);
		return res;
	}

	public ArrayList<Value> getUses() {
		ArrayList<Value> res = new ArrayList<>();
		res.add(value);
		res.add(pointer);
		return res;
	}

	public Value getDef() {
		return new NullValue();
	}
}