package IR;

import MIPS.MIPSER;
import TEMP.TEMP;
import TEMP.TEMP_FACTORY;
import TEMP.REGISTER;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class IRCommand_Call extends IRcommand {

    TEMP where;
    List<REGISTER> toSave;
    public IRCommand_Call(TEMP addrContainer) {
        where = addrContainer;
    }

    public List<TEMP> tempsUsed() { return Collections.singletonList(where); }

    // todo: how do we know that this wrecked a lot of registers later on?
    public TEMP dst() { return null; }

    @Override
    public void MIPSme() {
        MIPSER.getInstance().call(where);
    }

    public boolean hasSideEffects() {return true;}


    public static class Syscall extends IRCommand_Call {
        CRTSyscall type;
        public Syscall(CRTSyscall type) {
            super(TEMP_FACTORY.getInstance().getImmediateTEMP(type.name()));
            this.type = type;
        }
        public void MIPSme() {
            MIPSER.getInstance().syscall(type);
        }
    }
}
