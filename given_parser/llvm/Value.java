package llvm;

import ast.*;
import cfg.*;
public interface Value {

	public LLVMObject getType();
	public String getName();
	
}