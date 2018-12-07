package llvm;

import ast.*;
import cfg.*;

public class ConstantImmed implements LatticeCell {

	public String value;

	public ConstantImmed(String value) {
		this.value = value;
	}

	public String toString() {
		return "Constant " + value;
	}	
}