/***********/
/* PACKAGE */
/***********/
package TEMP;

/*******************/
/* GENERAL IMPORTS */
/*******************/

public class TEMP
{
	public enum REGISTER_NAME {
		t0(reg_class.TEMPO),
		t1(reg_class.TEMPO),
		t2(reg_class.TEMPO),
		t3(reg_class.TEMPO),
		t4(reg_class.TEMPO),
		t5(reg_class.TEMPO),
		t6(reg_class.TEMPO),
		t7(reg_class.TEMPO),
		TEMP_REGISTER_BOUNDRY,
		s0(reg_class.SAVED),
		s1(reg_class.SAVED),
		s2(reg_class.SAVED),
		s3(reg_class.SAVED),
		s4(reg_class.SAVED),
		s5(reg_class.SAVED),
		s6(reg_class.SAVED),
		s7(reg_class.SAVED),

		a0(reg_class.ARG),
		a1(reg_class.ARG),
		a2(reg_class.ARG),
		a3(reg_class.ARG),

		v0(reg_class.VALUE),
		v1(reg_class.VALUE),
		sp,
		fp,
		ra,
		zero,
		UNASSIGNED;

		enum reg_class { ARG, TEMPO, SPECIAL, SAVED, VALUE};
		reg_class flags;
		int indexInClass;
		REGISTER_NAME() {this(reg_class.SPECIAL);}
		REGISTER_NAME(reg_class f) {
			flags = f;
			this.indexInClass = this.flags != reg_class.SPECIAL ? Integer.parseInt(this.name().substring(1)) : 0;
		}
		public boolean arg() {
			return this.flags == reg_class.ARG;
		}
		public boolean temp() {
			return this.flags == reg_class.TEMPO;
		}
		public boolean saved() {
			return this.flags == reg_class.SAVED;
		}
		public int indexInClass() {
			return indexInClass;
		}
	}
	protected int serial = 0;
	protected REGISTER_NAME register = REGISTER_NAME.UNASSIGNED;
	public TEMP(int serial)
	{
		this.serial = serial;
	}
	
	public int getSerialNumber()
	{
		return serial;
	}

	public String toString() {
		return register == REGISTER_NAME.UNASSIGNED ?
				String.format("TEMP_%d", serial) :
				"$" + register.toString();
	}

	public void setRegister(int i) {
		assert i < REGISTER_NAME.TEMP_REGISTER_BOUNDRY.ordinal();
		this.register = REGISTER_NAME.values()[i];
	}

	public REGISTER_NAME register() {
		return this.register;
	}
}

