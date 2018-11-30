package llvm;
import ast.*;
import cfg.*;

public class NullValue implements Value {

	String type;

	public NullValue() {
	}

	public NullValue(String type) {
		this.type = type;
	}

	public String nullType() {
		return this.type;
	}

	public LLVMObject getType() {
		return new IntObject();
	}

	public String getName() {
		return "i32*";
	}

	public String toString() {
		return "null";
	}

}