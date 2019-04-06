package IR;

import MIPS.MIPSER;
import TEMP.*;

import java.util.Collections;
import java.util.List;

public class IRcommand_Inc extends IRcommand_MathOpBase.Add {
    public IRcommand_Inc(TEMP toinc) {
        super(toinc, toinc, new TEMP_IMMEDIATE(1), false);
    }

    @Override
    public void MIPSme() {
        assert !(dst instanceof TEMP_IMMEDIATE);
        super.MIPSme();
    }

    @Override
    public TEMP dst() {
        return dst;
    }

    @Override
    public List<TEMP> tempsUsed() {
        return Collections.singletonList(dst);
    }
}
