package llvm;

import java.util.*;

public class Register {

	private static int counter = 0;
	private String value;
	private String name;

	public Register (String value) {
		name = "r" + counter++;
		this.value = value;
	}

	@Override
	public String toString() {
		return name;
	}
}