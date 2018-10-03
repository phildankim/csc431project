package cfg;

import java.util.*;
import ast.*;

public class CFGFactory {

	public CFGFactory() {
	}

	public static ArrayList<CFG> createAllCFG(List<Function> functions) {
		
		ArrayList<CFG> cfgs = new ArrayList<CFG>();

		for (Function f : functions) {
			CFG cfg = new CFG();
			cfg.createCFG(f);
			cfgs.add(cfg);
		}

		return cfgs;
	}
}