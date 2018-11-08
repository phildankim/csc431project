package llvm;

public class StructObject implements LLVMObject {
	
	public String structName;
	public String id;

	public StructObject(String structName) {
		this.structName = structName;
	}

	public StructObject(String structName, String id) {
		this.structName = structName;
		this.id = id;
	}
	
	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.structName;
	}

	public String toString() {
		return "%struct." + this.structName + "*";
	}
}