package llvm;

public class StructField {
	String field;
	int index;
	StructField next;

	public StructField(String field, int index) {
		this.field = field;
		this.index = index;
		this.next = null;
	}

	public String toString() {
		return "Field: " + this.field + ", Index: " + this.index;
	}
}