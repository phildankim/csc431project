package cfg;

import java.util.*;
import ast.*;

public class CFGFactory {

	public CFGFactory() {
	}

	public static ArrayList<CFG> createAllCFG(List<Function> functions) {
		
		ArrayList<CFG> cfgs = new ArrayList<CFG>();

		for (Function f : functions) {
			CFG cfg = new CFG(f.getName());
			cfg.createCFG(f.getBody());
			cfg.connectToExit();
			cfgs.add(cfg);

		}

		return cfgs;
	}
}