package llvm;

import java.util.*;

public class InstructionCall implements Instruction {

	public Value result;
	public LLVMObject type;
	public String funcptr;
	public ArrayList<Value> args;
	public String argList;

	public InstructionCall (Value result, LLVMObject type, String funcptr, ArrayList<Value> args) {
		this.result= result;
		this.type = type;
		this.funcptr = funcptr;
		this.args = args;
		this.argList = buildArgList(args);
	}


	public InstructionCall (LLVMObject type, String funcptr, ArrayList<Value> args) {
		this.result= new Immediate("VOID", new IntObject());
		this.type = type;
		this.funcptr = funcptr;
		this.args = args;
		this.argList = buildArgList(args);
	}

	public String buildArgList(ArrayList<Value> args) {
		String returnString = "(";

		for (int i = 0; i < args.size(); i++) {
			Value reg = args.get(i);
			returnString += (reg.getType() + " " + reg.getName());

			if (i != (args.size() -1)) {
				returnString += ", ";
			}
		}

		returnString += ")";

	return returnString;

	}


	@Override
	public String toString() {

		if (result instanceof Immediate) {
			return "call " + type + " @" + funcptr + argList;
		}
		else {
			return result + " = call " + type + " @" + funcptr + argList;
		}
	}
}