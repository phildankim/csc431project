package llvm;

public class IntObject implements LLVMObject {

	public String value;

	public IntObject(String value) {
		this.value = value;
	}

	public String toString() {
		return "i32";
	}
}