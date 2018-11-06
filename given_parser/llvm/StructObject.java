package llvm;

import java.util.*;
import ast.*;

public class StructObject implements LLVMObject {
	
	public String id;
	public ArrayList<LLVMObject> fields = new ArrayList<LLVMObject>();

	public StructObject(String id, List<Declaration> fields) {
		this.id = id;
		//StructObject.convertAndAddField(fields);

	}


	// public static void convertAndAddField(List<Declaration> declarations) {
	// 	for (Declaration d : declarations) {
	// 		Type t = d.getType();

	// 		if (t instanceof IntType) {
	// 			this.fields.add
	// 		}
	// 		else if (t instanceof BoolType) {
	// 			field = new StructField(d.getName(), fields.size() + 1);
	// 			fields.add(field);
	// 		}
	// 		else if (t instanceof StructType) {
	// 			StructType st = (StructType)t;
	// 			String structName = st.getName();
	// 			field = new StructField(structName, fields.size() + 1);
	// 			fields.add(field);
	// 		}
	// 		else if (t instanceof VoidType) {
	// 		}
	// 		else {
	// 		} 
	// 	}
			
	// }

}