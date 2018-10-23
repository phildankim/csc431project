package llvm;

public class InstructionCall implements Instruction {

	String result;
	String type;
	String funcptr;
	ArrayList<String> args;
	String argList;

	public InstructionAdd (String result, String type, String funcptr, ArrayList<String> args) {
		this.result= result;
		this.type = type;
		this.funcptr = funcptr;
		this.args = args;
		this.argList = buildArgList(args);
	}



	@Override
	public String toString() {
		if (type.equals("void")) {
			return "CALL NOT FINISHED call ";
		}
		else {
			return result + "CALL NOT FINSIHED  = ";
		}
	}
}