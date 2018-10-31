package llvm;

import java.util.*;

public class StructObject implements LLVMObject {

	private String id;
	private ArrayList<LLVMObject> fields = new ArrayList<LLVMObject>();

	public StructObject(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public String toString() {
		return "%struct." + this.getId() + "*";
	}

	public void addField(LLVMObject field) {
		this.fields.add(field);
	}

	public ArrayList<LLVMObject> getFields() {
		return this.fields;
	}

	public LLVMObject getField(String field) {
		for (LLVMObject f : this.getFields()) {
			if (f.getId().equals(field)) {
				return f;
			}
		}
		return null;
	}

	public int getFieldIndex(LLVMObject field) {
		int counter = 0;
		for (LLVMObject f : this.getFields()) {
			if (f.getId().equals(field.getId())) {
				return counter;
			}
			counter++;
		}
		return -1;
	}
}