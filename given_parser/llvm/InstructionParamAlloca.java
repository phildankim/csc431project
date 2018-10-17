package llvm;

import ast.*;
import java.util.*;

public class InstructionParamAlloca implements Instruction {

	String result;
	String type;

	public InstructionParamAlloca(Declaration d) {
		this.type = convertTypeToString(d);
		this.result = "%_P_" + d.getName();
	}

	public String convertTypeToString(Declaration d) {
		Type t = d.getType();

		if (t instanceof IntType) {
			return("i32");
		}
		else if (t instanceof BoolType) {
			return("i32");
		}
		else if (t instanceof StructType) {
			StructType st = (StructType)t;

			String structName = st.getName();
			return("%struct." + structName + "*");
		}
		else if (t instanceof VoidType) {
			return "voidtype";
		}
		else {
			return "somethingelsetype";
		}
	}

	@Override
	public String toString() {
		return this.result + " = alloca " + this.type;
	}
}