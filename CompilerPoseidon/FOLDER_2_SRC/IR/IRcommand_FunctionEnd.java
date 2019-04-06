package IR;

import MIPS.MIPSER;
import TEMP.REGISTER;

public class IRcommand_FunctionEnd extends IRcommand{
    public IRcommand_FunctionEnd() {
    }

    @Override
    public void MIPSme() {
        // discard register state her
        MIPSER.getInstance().jr(REGISTER.ra);
    }
}
