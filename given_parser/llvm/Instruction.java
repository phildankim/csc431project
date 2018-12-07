package llvm;

import java.util.*;

public interface Instruction {
	public ArrayList<Value> getRegisters();
	public Value getDef();
	public ArrayList<Value> getUses();
}