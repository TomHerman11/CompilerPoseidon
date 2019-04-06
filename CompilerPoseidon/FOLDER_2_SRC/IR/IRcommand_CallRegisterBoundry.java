package IR;

import MIPS.MIPSER;
import TEMP.REGISTER;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class IRcommand_CallRegisterBoundry extends IRcommand implements RegisterBoundry {
    List<REGISTER> regs;
    boolean enter;
    public IRcommand_CallRegisterBoundry(boolean enter) {
        this.enter = enter;
    }

    @Override
    public void MIPSme() {
        if (enter)
            MIPSER.getInstance().saveAllArgs(regs);
        else
            MIPSER.getInstance().restoreAllArgs(regs);
    }

    @Override
    public BoundryType boundryType() {
        return BoundryType.local;
    }

    @Override
    public void saveRegisters(Set<REGISTER> names) {
        regs = new ArrayList<>(names);
    }
}
