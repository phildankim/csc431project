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

	@Override
	public String toString() {
		String res = "%struct." + this.name + " =  type {";
		for (String s : this.types) {
			res += (s + ", ");
		}
		res += "}";
		return res;
	}

}