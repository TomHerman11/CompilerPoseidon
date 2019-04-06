package IR;

import MIPS.MIPSER;
import TEMP.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class IRcommand_StackCommand extends IRcommand{
    protected int index;
    protected TEMP out;
    // stack commands read and write locals. they work relative to ebp
    protected IRcommand_StackCommand(TEMP out, int index) {
        this.index = index;
        this.out = out;
    }
    protected int indexToOffset() {
        return -(this.index * 4);
    }

    public static List<IRcommand> Write(TEMP what, int index) {
        if (what instanceof TEMP_IMMEDIATE) {
            IRcommand cmds[] = new IRcommand[2];
            TEMP intermediate = TEMP_FACTORY.getInstance().getFreshTEMP();
            cmds[0] = new IRcommand_Move(intermediate, what);
            cmds[1] = new IRcommand_StackCommand.WriteCommand(intermediate, index);
            return Arrays.asList(cmds);
        }
        return Collections.singletonList(new IRcommand_StackCommand.WriteCommand(what, index));
    }

    public static class WriteCommand extends IRcommand_StackCommand {
        public WriteCommand(TEMP out, int index) {
            super(out, index);
        }

        @Override
        public void MIPSme() {
            MIPSER.getInstance().store(out, REGISTER.fp, indexToOffset());
        }

        @Override
        public List<TEMP> tempsUsed() {
            return Collections.singletonList(out);
        }
        public String toString() {return getClass().getCanonicalName() + out.toString() + String.format(" toIndex: %d", index);}
    }

    public static List<IRcommand> Read(TEMP to, int index) {
        return Collections.singletonList(new ReadCommand(to, index));
    }

    public static class ReadCommand extends IRcommand_StackCommand {
        public ReadCommand(TEMP out, int index) {
            super(out, index);
        }

        @Override
        public void MIPSme() {
            assert !(out instanceof TEMP_IMMEDIATE);
            MIPSER.getInstance().load(out, REGISTER.fp, indexToOffset());
        }

        @Override
        public TEMP dst() {
            return out;
        }
        public String toString() {return getClass().getCanonicalName() + out.toString() + String.format(" toIndex: %d", index);}
    }



}
