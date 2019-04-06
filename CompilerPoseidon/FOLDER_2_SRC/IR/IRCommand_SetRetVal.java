package IR;

import MIPS.MIPSER;
import TEMP.*;

import java.time.chrono.ThaiBuddhistEra;
import java.util.Collections;
import java.util.List;

public class IRCommand_SetRetVal extends IRcommand {
    private TEMP retval;
    public IRCommand_SetRetVal (TEMP retval) { this.retval = retval; }


    // we could also inherit the MIPSme of Add. currently not.
    @Override
    public void MIPSme() {
        if (retval != null) {
            IRcommand_Move.MIPSMove(REGISTER.v0, retval);
        }
    }

    @Override
    public List<TEMP> tempsUsed() {
        return Collections.singletonList(retval);
    }
}
