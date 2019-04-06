package IR;

import MIPS.MIPSER;
import TEMP.REGISTER;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// this class restores the callee arguments from the stack after a function call
public abstract class IRcommand_CallArgumentBoundry extends IRcommand implements RegisterBoundry {
    List<REGISTER> regs;
    public static class Restore extends IRcommand_CallArgumentBoundry {
        @Override
        public void MIPSme() {
            MIPSER.getInstance().regsStackLoadArgsOnly(regs);
        }

        @Override
        public BoundryType boundryType() {
            return BoundryType.args;
        }

        @Override
        public void saveRegisters(Set<REGISTER> names) {
            regs = new ArrayList<>(names);
        }
    }

    public static class Save extends IRcommand_CallArgumentBoundry {
        @Override
        public void MIPSme() {
            MIPSER.getInstance().regsStackStoreArgsOnly(regs);
        }

        @Override
        public BoundryType boundryType() {
            return BoundryType.args;
        }

        @Override
        public void saveRegisters(Set<REGISTER> names) {
            regs = new ArrayList<>(names);
        }
    }
}
