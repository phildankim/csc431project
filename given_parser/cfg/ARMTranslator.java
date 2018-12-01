package cfg;

import ast.*;
import llvm.*;
import java.util.*;

public class ARMTranslator {
	public static String translate(Instruction i) {

		StringBuilder sb = new StringBuilder();

		if (i instanceof InstructionAdd) {
			InstructionAdd ia = (InstructionAdd)i;

			if (ia.operand1 instanceof Immediate && ia.operand2 instanceof Register) {
				sb.append("\tmovw t9, #lower16:" + ((Immediate)ia.operand1).getValue() + "\n");
				sb.append("\tmovw t9, #upper:" + ((Immediate)ia.operand1).getValue() + "\n");
				sb.append("\tadd " + ia.register + ", " + ia.operand2 + ", " + "t9\n" );
			}

			else if (ia.operand2 instanceof Immediate && ia.operand1 instanceof Register) {
				sb.append("\tmovw t9, #lower16:" + ((Immediate)ia.operand2).getValue() + "\n");
				sb.append("\tmovw t9, #upper:" + ((Immediate)ia.operand2).getValue() + "\n");
				sb.append("\tadd " + ia.register + ", " + ia.operand1 + ", " + "t9\n" );
			}

			else if (ia.operand1 instanceof Register && ia.operand2 instanceof Register) {
				sb.append("\tadd " + ia.register + ", " + ia.operand1 + ", " + ia.operand2 + "\n");
			}
		}

		sb.append(i.toString());

		return sb.toString();
	}
}