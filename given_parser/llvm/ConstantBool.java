package llvm;

import ast.*;
import cfg.*;

public class ConstantBool implements LatticeCell {

	public boolean bool;

	public ConstantBool(boolean bool) {
		this.bool = bool;
	}

	public String toString() {
		if (bool) {
			return "Constant True";
		}
		else return "Constant False";
	}	
}