package llvm;

import java.util.*;
import ast.*;
import cfg.*;

public class InstructionDecl implements Instruction {

	public String name;
	public Type type;

	public InstructionDecl(Declaration decl) {
		this.name = decl.getName();
		this.type = decl.getType();
	}

	@Override
	public String toString() {

		if (type instanceof StructType) {
			StructType tt = (StructType)type;
			String structType = tt.getName();

			return "@" + name + " = common global %struct." + structType + "* null, align 8";
		}
		else if (type instanceof IntType) {
			IntType it = (IntType)type;
			return "@" + name + " = common global i32 0";
		}
		else if (type instanceof BoolType) {
			return "@" + name + " = common global i32 0";
		}
		else {
			return "";
		}
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