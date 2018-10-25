package llvm;

import java.util.*;

public class InstructionCall implements Instruction {

	String result;
	String type;
	String funcptr;
	ArrayList<String> args;
	String argList;

	public InstructionCall (String result, String type, String funcptr, ArrayList<String> args) {
		this.result= result;
		this.type = type;
		this.funcptr = funcptr;
		this.args = args;
		this.argList = buildArgList(args);
	}

	public String buildArgList(ArrayList<String> args) {
		String returnString = "(";

		for (int i = 0; i < args.size(); i++) {
			returnString += "i32 " + args.get(i);

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
			return result + " = call i32 @" + funcptr + argList;
		}
	}
}