package llvm;

public class BoolObject implements LLVMObject {
	
	public String id;

	public BoolObject() {
	}

	public BoolObject(String id) {
		this.id = id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public String toString() {
		return "i32";
	}
}