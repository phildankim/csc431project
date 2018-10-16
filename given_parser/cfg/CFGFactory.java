package cfg;

import java.util.*;
import ast.*;

public class CFGFactory {

	public CFGFactory() {
	}

	public static ArrayList<CFG> createAllCFG(Program p) {
		
		ArrayList<CFG> cfgs = new ArrayList<CFG>();
		List<Function> functions = p.getFuncs();

		for (Function f : functions) {
			CFG cfg = new CFG(f.getName(), p);
			cfg.createCFG(f.getBody());
			cfg.connectToExit();
			cfgs.add(cfg);
			cfg.addPredecessorsAndSuccessors();
		}

		return cfgs;
	}
}