package IR;

import MIPS.MIPSER;
import TEMP.TEMP;

public class IRcommand_Exit extends IRcommand {
    public IRcommand_Exit(TEMP out) {
        super();
    }

    @Override
    public void MIPSme() {
        MIPSER.getInstance().exit();
    }
}
