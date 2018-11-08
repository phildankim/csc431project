package llvm;

public class IntObject implements LLVMObject {
	
	public String value;
	public String id;

	public IntObject() {
	}

	public IntObject(String id) {
		this.id = id;
	}

	public IntObject(String id, String value) {
		this.id = id;
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}

	public String getId() {
		return this.id;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String toString() {
		return "i32";
	}
}