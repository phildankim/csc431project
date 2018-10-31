package llvm;

public class IntObject implements LLVMObject {

	String id;

	public IntObject(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public String toString() {
		return "i32";
	}
}