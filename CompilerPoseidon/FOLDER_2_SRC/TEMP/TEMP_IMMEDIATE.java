package TEMP;

public class TEMP_IMMEDIATE extends TEMP {
	Object value;
	public TEMP_IMMEDIATE(Object value) {
		super(0xFFFFFFFF);
		this.value = value;
	}

	@Override
	public String toString() {
		return value.toString();
	}

	public String labelName() {
		assert false; // shouldn't happen
		return this.toString();
	}

	public static class TEMP_INT extends TEMP_IMMEDIATE{
		public TEMP_INT(int value) {
			super(value);
		}
		public Integer value() {return (Integer) this.value; }

		public void mult(int i) {
			value = ((Integer)value) * i;
		}
		public void add(int i) {
			value = ((Integer)value) + i;
		}
	}

	public static class TEMP_LABEL extends TEMP_IMMEDIATE {
		TEMP_LABEL(String value) {
			super(value);
		}

		public String value() {return (String) this.value; }
	}
}

