package llvm;

public class VoidObject implements LLVMObject {

	String id;

	public VoidObject(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public String toString() {
		return "void";
	}
}