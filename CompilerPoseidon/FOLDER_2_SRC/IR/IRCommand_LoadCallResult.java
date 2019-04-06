package IR;

import MIPS.MIPSER;
import TEMP.TEMP;
import TEMP.REGISTER;

import java.util.List;


public class IRCommand_LoadCallResult extends IRcommand {
    TEMP out;
    public IRCommand_LoadCallResult(TEMP out) {
        super();
        this.out = out;
    }

    @Override
    public List<TEMP> tempsUsed() {
        // todo: consider how to add "v0" to this list
        return super.tempsUsed();
    }
    @Override
    public void MIPSme() {
        MIPSER.getInstance().move(out, REGISTER.v0);
    }
    public TEMP dst() { return out; }
}
