package IR;

import TEMP.TEMP;

public class IRcommand_LoadThisArg extends IRCommand_Arg.Load {
    // this reads the 'this' arg from somewhere in the function context, it's usually in $a0.
    public IRcommand_LoadThisArg(TEMP thisarg) {
        super(thisarg, 0);
    }

}
