package cfg;

import java.util.*;
import src.ast.*;

public class CFGFactory {

	public List<CFG> cfgs;

	public CFGFactory() {

	}

	public static void createAllCFG(List<Function> functions) {
		for (Function f : function) {
			CFG cfg = new CFG();
			cfgs.add(cfg.createCFG(f));
		}
	}
}