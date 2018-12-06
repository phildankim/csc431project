package llvm;

import java.util.*;
import ast.*;

public class InstructionTypeDecl implements Instruction {

	public ArrayList<String> types = new ArrayList<String>();
	public String name;

	public InstructionTypeDecl(TypeDeclaration td) {
		convertFieldsToTypes(td);
		this.name = td.getName();
	}

	public void convertFieldsToTypes(TypeDeclaration td) {
		for (Declaration d : td.getFields()) {
			Type t = d.getType();

			if (t instanceof IntType) {
				types.add("i32");
			}
			else if (t instanceof BoolType) {
				types.add("i32");
			}
			else if (t instanceof StructType) {
				StructType st = (StructType)t;

				String structName = st.getName();
				types.add("%struct." + structName + "*");
			}
			else if (t instanceof VoidType) {

			}
			else {

			}

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

	@Override
	public String toString() {
		String pre = "%struct." + this.name + " = type ";	
		StringBuilder builder = new StringBuilder("{");
		for (String s : this.types) builder.append(s).append(",");
		if (this.types.size() > 0) builder.deleteCharAt(builder.length() - 1);
		builder.append("}");
		builder.insert(0, pre);
		String res = builder.toString();
		return res;
	}

}