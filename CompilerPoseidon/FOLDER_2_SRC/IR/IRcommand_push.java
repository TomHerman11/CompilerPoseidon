package IR;

import MIPS.MIPSER;
import TEMP.TEMP;

import java.util.Collections;
import java.util.List;

public class IRcommand_push extends IRcommand {
    TEMP arg;
    public IRcommand_push(TEMP arg) {
        super();
        this.arg = arg;
    }

    @Override
    public void MIPSme() {
        MIPSER mipser = MIPSER.getInstance();
        mipser.push(arg);
    }

    @Override
    public List<TEMP> tempsUsed() {
        return Collections.singletonList(arg);
    }
}
