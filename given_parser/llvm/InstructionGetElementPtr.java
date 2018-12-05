package llvm;

public class InstructionGetElementPtr implements Instruction{
	
	public Value result;
	public LLVMObject type;
	public Value ptrval;
	public String index;

	public InstructionGetElementPtr (Value result, LLVMObject type, Value ptrval, String index) {
		this.result = result;
		this.type = type;
		this.ptrval = ptrval;
		this.index = index;
	}

	@Override
	public String toString() {
		return result + " = getelementptr " + type + " " + ptrval + ", i1 0, i32 " + index;
	}
}