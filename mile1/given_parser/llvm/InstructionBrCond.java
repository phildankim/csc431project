public class InstructionBrCond {
	
	String condition;
	String labelTrue;
	String labelFalse;

	public InstructionBrCond (String condition, String labelTrue, String labelFalse) {
		this.condition = condition;
		this.labelTrue = labelTrue;
		this.labelFalse = labelFalse;
	}

	@Override
	public String toString() {
		return "br i1 " + condition + ", label " + labelTrue + ", label " + labelFalse; 
	}
}