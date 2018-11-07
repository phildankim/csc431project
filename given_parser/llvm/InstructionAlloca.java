package llvm;

import ast.*;
import java.util.*;

public class InstructionAlloca implements Instruction {

	String result;
	Type type;

	public InstructionAlloca(Type type, String res) {
		this.type = type;
		this.result = res;
	}


	@Override
	public String toString() {
		return this.result + " = alloca " + this.type.toString();
	}
}