package llvm;
import ast.*;
import cfg.*;

public class Immediate implements Value {

	String value;
	LLVMObject type;

	public Immediate(String value, LLVMObject type) {
		this.value = value;
		this.type = type;
	}

	public Immediate (String value) {
		this.value = value;
		this.type = new IntObject();
	}

	public LLVMObject getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	public String getName() {
		return value;
	}

	public String toString() {
		return value;
	}

}