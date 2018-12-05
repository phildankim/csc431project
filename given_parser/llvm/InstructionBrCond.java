package llvm;

public class InstructionBrCond implements Instruction{
	

	public Value condition;
	public String labelTrue;
	public String labelFalse;

	public InstructionBrCond (Value condition, String labelTrue, String labelFalse) {
		this.condition = condition;
		this.labelTrue = labelTrue;
		this.labelFalse = labelFalse;
	}

	@Override
	public String toString() {
		return "br i1 " + condition + ", label %" + labelTrue + ", label %" + labelFalse; 
	}
}