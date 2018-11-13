package llvm;
import ast.*;
import cfg.*;

public class Immediate implements Value {

	String value;

	public Immediate(String value) {
		this.value = value;
	}

	public LLVMObject getType() {
		return new IntObject();
	}

}