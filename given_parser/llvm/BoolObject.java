package llvm;

public class BoolObject implements LLVMObject {

	String id;

	public BoolObject(String id) {
		this.id = id;
	}
	public String getId() {
		return this.id;
	}

	public String toString() {
		return "i32";
	}
}