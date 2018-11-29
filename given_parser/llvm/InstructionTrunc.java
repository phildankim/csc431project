	package llvm;

public class InstructionTrunc implements Instruction {
	
	Value result;
	Value register;

	public InstructionTrunc (Value result, Value register) {
		this.result = result;
		this.register = register;

	}

	@Override
	public String toString() {
		return result + " = trunc " + register.getType() + " " + register + " to i1";
	}
}