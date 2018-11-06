package llvm;

public class BoolObject implements LLVMObject {
	
	public String value;

	public BoolObject(String value) {
		this.value = value;
	}
	
	public String toString() {
		return "i32";
	}
}