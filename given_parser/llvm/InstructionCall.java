package llvm;

import java.util.*;

public class InstructionCall implements Instruction {

	String result;
	LLVMObject type;
	String funcptr;
	ArrayList<Register> args;
	String argList;

	public InstructionCall (String result, LLVMObject type, String funcptr, ArrayList<Register> args) {
		this.result= result;
		this.type = type;
		this.funcptr = funcptr;
		this.args = args;
		this.argList = buildArgList(args);
	}

	public String buildArgList(ArrayList<Register> args) {
		String returnString = "(";

		for (int i = 0; i < args.size(); i++) {
			// returnString += (type + " " + args.get(i));
			Register reg = args.get(i);
			returnString += (reg.getType() + " " + reg.getRegNum());

			if (i != (args.size() -1)) {
				returnString += ", ";
			}
		}

		returnString += ")";

	return returnString;

	}


	@Override
	public String toString() {
		if (result.equals("VOID")) {
			return "call void @" + funcptr + argList;
		}
		else {
			return result + " = call " + type + " @" + funcptr + argList;
		}
	}
}