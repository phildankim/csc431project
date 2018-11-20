package llvm;
import ast.*;
import cfg.*;

public class NullValue implements Value {

	public NullValue() {
	}

	public LLVMObject getType() {
		return new StructObject("");
	}

	public String getName() {
		return "0";
	}

	public String toString() {
		return "0";
	}

}