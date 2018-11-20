package llvm;

import ast.*;
import cfg.*;


public class PhiOperand {

	String id;
	Value value;

	public PhiOperand (String id, Value value) {
		this.id = id;
		this.value = value;
	}

	public String getId() {
		return id;
	}

	public Value getValue() {
		return value;
	}
	
}