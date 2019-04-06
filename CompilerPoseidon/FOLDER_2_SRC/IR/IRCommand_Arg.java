package IR;

import MIPS.MIPSER;
import TEMP.*;
import java.util.Collections;
import java.util.List;

abstract public class IRCommand_Arg extends IRcommand {
    TEMP which;
    int index;
    public IRCommand_Arg(TEMP temp, int index) {
        which = temp;
        this.index = index;
    }

    protected int offset() {
        return (index - REGISTER.args.length) * 4 + IRcommand_FunctionPrologue.argOffsetFromBP();
    }


    public static class Load extends IRCommand_Arg{
        public Load(TEMP temp, int index) {
            super(temp, index);
        }

        @Override
        public void MIPSme()
        {
            MIPSER mipser = MIPSER.getInstance();
            if (index < 4) {
                mipser.move(which, REGISTER.arg(index));
            }
            else {
                MIPSER.getInstance().load(which, REGISTER.fp, /*((index - 3) * 4)*/ offset());
            }
        }


        // todo: study the change of the sp register here, consider adding to changed
        public TEMP dst() { return which; }

        @Override
        public List<TEMP> tempsUsed() {
            if (index < 4)
                return Collections.singletonList(REGISTER.arg(index));
            else return super.tempsUsed();
        }

    }
    public static class Store extends IRCommand_Arg{
        public Store(TEMP temp, int index) {
            super(temp, index);
        }

        @Override
        public void MIPSme() {
            if (index < 4) {
                IRcommand_Move.MIPSMove(REGISTER.arg(index), which);
            }
            else {
                MIPSER.getInstance().store(which, REGISTER.fp,
                        /*((index - 3) * 4)*/ offset());
            }
        }
        public List<TEMP> tempsUsed() {
            // todo: consider adding the argument register as a special temp here.
            return Collections.singletonList(which);
        }
    }

    public static class Pass extends IRCommand_Arg.Store {
        int stackArgs;
        public Pass(TEMP temp, int index, int totalArgCount) {
            super(temp, index);
            // I may have args put on the stack. if I do, it's if there are more than args.lengh.
            this.stackArgs = Math.max(totalArgCount - REGISTER.args.length, 0);
        }
        @Override
        public void MIPSme() {
            if (index < 4) {
                IRcommand_Move.MIPSMove(REGISTER.arg(index), which);
            }
            else {
                // 5th arg - index is 4, store index is 0
                int storeIndex = this.index - REGISTER.args.length;
                TEMP pushed = which;
                if (which instanceof TEMP_IMMEDIATE) {
                    MIPSER.getInstance().li(REGISTER.v0, (TEMP_IMMEDIATE) which);
                    pushed = REGISTER.v0;
                }
                // 5th arg is first on the stack, it's index is 4.
                // stackArgs is 8 - 4. we want the offset to be 0.
                // 8th arg is last on the stack, index is 7. we want it's offset to be 0xC
                // overall we would store in 0x0, 0x4, 0x8, 0xc
                MIPSER.getInstance().store(pushed, REGISTER.sp,
                        storeIndex * 4);
            }
        }
    }

    public static class ClearAll extends IRCommand_Arg.Allocate {
        public ClearAll(int i) {
            super(i);
        }

        @Override
        public void MIPSme() {
            if (argSpace > 0) {
                MIPSER.getInstance().add(REGISTER.sp, REGISTER.sp,
                        TEMP_FACTORY.getInstance().getImmediateTEMP(argSpace));
            }
        }

        @Override
        public TEMP dst() {
            return null; //todo consider: index < 4 ? REGISTER.arg(index) : null;
        }
    }

    public static class Allocate extends IRcommand {
        int argSpace;
        public Allocate(int size) {
            argSpace = Math.max(size - REGISTER.args.length, 0) * 4;
        }

        @Override
        public void MIPSme() {
            if (argSpace > 0) {
                MIPSER.getInstance().sub(REGISTER.sp, REGISTER.sp,
                        TEMP_FACTORY.getInstance().getImmediateTEMP(argSpace));
            }
        }
    }
}
