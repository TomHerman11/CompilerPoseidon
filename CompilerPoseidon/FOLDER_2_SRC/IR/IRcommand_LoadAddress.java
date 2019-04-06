package IR;

import MIPS.MIPSER;
import TEMP.TEMP;

public class IRcommand_LoadAddress extends IRcommand {
    String label;
    TEMP target;
    public IRcommand_LoadAddress(TEMP t, String label) {
        super();
        this.label = label;
        this.target = t;
    }

    public void MIPSme() {
        MIPSER.getInstance().la(target, label);
    }

    public String toString() {
        return this.getClass().getSimpleName() + " " + dst() + ", " + label;
    }
    @Override
    public TEMP dst() {
        return target;
    }
}
