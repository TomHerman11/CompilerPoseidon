package IR;

import MIPS.MIPSER;
import TEMP.TEMP;

import java.util.Collections;

public class IRcommand_pop extends IRcommand {
    TEMP arg;
    public IRcommand_pop(TEMP arg) {
        super();
        this.arg = arg;
    }

    @Override
    public void MIPSme() {
        MIPSER mipser = MIPSER.getInstance();
        mipser.pop(arg);
    }

    @Override
    public TEMP dst() {
        return arg;
    }
}
