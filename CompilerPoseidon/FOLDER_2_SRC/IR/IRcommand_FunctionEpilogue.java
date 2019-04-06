package IR;

import MIPS.MIPSER;
import TEMP.*;
import TYPES.TYPE_FUNCTION;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class IRcommand_FunctionEpilogue extends IRcommand implements RegisterBoundry{
    private List<REGISTER> toLoad;
    private TYPE_FUNCTION myfunc;
    public IRcommand_FunctionEpilogue(TYPE_FUNCTION function_type) {
        super();
        myfunc = function_type;
    }

    @Override
    public void MIPSme() {
        // clear all the locals
        // load saved registers

        MIPSER mipser = MIPSER.getInstance();

        mipser.regsStackStash(toLoad, false);

        REGISTER sp = REGISTER.sp;
        REGISTER bp = REGISTER.fp;
        mipser.move(sp, bp); // restore sp to function base.
        // pop the base pointer
        mipser.load(bp, sp, 0);
        //clear the function name
        int cleanup_size = 4 + (myfunc.traceable ? 4 : 0);
        mipser.add(sp, sp, new TEMP_IMMEDIATE(cleanup_size));  // remove both saved fp and method name ptr.
        // jr the return register - in FUNCTION end
    }

    @Override
    public BoundryType boundryType() {
        return BoundryType.interFunction;
    }

    @Override
    public void saveRegisters(Set<REGISTER> names) {
        toLoad = new ArrayList<REGISTER>(names);
        for (int i = 0 ; i < Math.min(myfunc.argCount(), 4); ++i) {
            toLoad.add(i, REGISTER.arg(i));
        }
    }
}
