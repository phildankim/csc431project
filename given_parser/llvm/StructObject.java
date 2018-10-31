package llvm;

public class StructObject implements LLVMObject {

	private String name;

	public StructObject(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public String toString() {
		return "%struct." + this.getName() + "*";
	}
}