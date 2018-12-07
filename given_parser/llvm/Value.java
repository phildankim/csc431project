package llvm;

import ast.*;
import cfg.*;
import java.util.*;
public interface Value {

	public LLVMObject getType();
	public String getName();
	
}