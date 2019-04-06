package IR;

import MIPS.MIPSER;
import TYPES.TYPE_FUNCTION;
import TEMP.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class IRcommand_FunctionPrologue extends IRcommand implements RegisterBoundry{
    final private TYPE_FUNCTION myfunc;
    List<REGISTER> toSave;
    public IRcommand_FunctionPrologue(TYPE_FUNCTION function_type) {
        super();
        myfunc = function_type;
    }

    public static int argOffsetFromBP() {
        return 8;
    }

    @Override
    public void MIPSme() {

        // stack frame works like:
        //
        // LOCAL_n  |<-- SP / BP - 4n
        // ...
        // LOCAL_1  |<-- BP - 4     ^
        // fp       |<-- BP         |
        // metadata |<-- BP + 4     |
        // ARG4     |<-- BP + 8     |
        // ARG5     |<-- BP + C     |
        // emit a the function name as a label. use this name:
        REGISTER sp = REGISTER.sp;
        MIPSER mipser = MIPSER.getInstance();
//        name is handled in IRFunctionStart
//        String name = myfunc.fullyQualifiedName();
//        mipser.label(name);
        // save the method name for the PrintTrace function
        if (myfunc.traceable) { // doesn't happen for c'tors.
            mipser.la(REGISTER.t0, IR_DATA_SECTION.getInstance().getFunctionNameDataLabel(myfunc).toString());
            mipser.push(REGISTER.t0);
        }
        // save the stack base pointer
        mipser.push(REGISTER.fp);
        mipser.move(REGISTER.fp, sp);
        // move stack pointer to stack base
        // change the stack pointer by however many locals needed
        int stackFrameSlackSpace = myfunc == null ? 0 : myfunc.localCount();
        stackFrameSlackSpace += toSave == null? 0 : toSave.size();
        stackFrameSlackSpace += Math.min(myfunc.argCount(), 4) * 4; // for the args that pass in regs
        // note that args are accessible via myfunc as well, if needed
        if (stackFrameSlackSpace != 0) {
            mipser.sub(sp, sp, new TEMP_IMMEDIATE(4 * (stackFrameSlackSpace)));
        }
        mipser.regsStackStash(toSave, true);
    }

    @Override
    public BoundryType boundryType() {
        return BoundryType.interFunction;
    }

    @Override
    public void saveRegisters(Set<REGISTER> names) {
        toSave = new ArrayList<REGISTER>(names);
        for (int i = 0 ; i < Math.min(myfunc.argCount(), 4); ++i) {
            toSave.add(i, REGISTER.arg(i));
        }
    }
}
