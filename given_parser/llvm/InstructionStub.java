package llvm;

import llvm.*;
import java.util.*;

public class InstructionStub implements Instruction{

	public String message;

	public InstructionStub(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "STUB INSTRUCTION FROM " + message ;
	}

	public ArrayList<Value> getRegisters() {
		ArrayList<Value> res = new ArrayList<>();

		return res;
	}

	public ArrayList<Value> getUses() {
		ArrayList<Value> res = new ArrayList<>();

		return res;
	}

	public Value getDef() {
		return new NullValue();
	}
}