package cfg;

import java.util.*;
import ast.*;
import llvm.*;

public class CFGFactory {

	public CFGFactory() {
	}

	public static ArrayList<CFG> createAllCFG(Program p) {
		
		ArrayList<CFG> cfgs = new ArrayList<CFG>();
		List<Function> functions = p.getFuncs();

		for (Function f : functions) {
			CFG cfg = new CFG(f, p);
			cfg.createCFG(f.getBody());
			cfg.connectToExit();
			cfg.addTerminationInstructionToExit();
			cfgs.add(cfg);
			cfg.addPredecessorsAndSuccessors();
			//Register.printRegisters();
			cfg.clearStructs();
			cfg.removeEmptyBlocks();
			cfg.removeUnnecessaryBranch();
		}

		return cfgs;
	}
}