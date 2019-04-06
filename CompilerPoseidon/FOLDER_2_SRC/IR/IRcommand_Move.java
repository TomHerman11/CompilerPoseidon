package IR;

import MIPS.MIPSER;
import TEMP.*;

import java.util.Collections;
import java.util.List;

public class IRcommand_Move extends IRcommand {
    private TEMP dst, op1;
    public IRcommand_Move(TEMP t, TEMP assignee) {
        this.dst = t;
        this.op1 = assignee;
    }

    static public void MIPSMove(TEMP where, TEMP what) {
        if (what instanceof TEMP_IMMEDIATE.TEMP_LABEL) {
            MIPSER.getInstance().la(where, what.toString());
        }
        else if (what instanceof TEMP_IMMEDIATE) {
            MIPSER.getInstance().li(where, (TEMP_IMMEDIATE)what);
        }
        else {
            MIPSER.getInstance().move(where, what);
        }
    }

    // we could also inherit the MIPSme of Add. currently not.
    @Override
    public void MIPSme() {
        MIPSMove(this.dst, this.op1);
    }

    @Override
    public List<TEMP> tempsUsed() {
        return Collections.singletonList(op1);
    }
    public TEMP dst() { return dst; }
}
