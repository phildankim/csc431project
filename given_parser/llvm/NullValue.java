package llvm;
import ast.*;
import cfg.*;

public class NullValue implements Value {

	LLVMObject type;

	public NullValue() {
		this.type = null;
	}

	public NullValue(LLVMObject type) {
		this.type = type;
	}

	public void setType(LLVMObject type) {
		this.type = type;
	}

	public LLVMObject getType() {
		if (this.type != null) {
			return this.type;
		}
		return new IntObject();
	}

	public String getName() {
		return "null";
	}

	public String toString() {
		return "null";
	}

}