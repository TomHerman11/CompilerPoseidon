package IR;

import MIPS.MIPSER;
import TEMP.TEMP_IMMEDIATE;

public class IRcommand_Label extends IRcommand {
    TEMP_IMMEDIATE label;
    public IRcommand_Label(TEMP_IMMEDIATE label) {
        this.label = label;
    }

    @Override
    public void MIPSme() {
        MIPSER.getInstance().label(label.toString());
    }

    @Override
    public String toString() {
        return this.label.toString() + ":";
    }

    public String labelName() {
        return this.label.toString();
    }
	
	public String pure_labelName() {
		return labelName();
	}
}
