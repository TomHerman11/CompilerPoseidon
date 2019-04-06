package IR;

import TEMP.TEMP;

import java.util.List;

public class IRCommand_ClearArg extends IRcommand {
    private int indexToPop;
    public IRCommand_ClearArg(int i) {
        indexToPop = i;
    }

    // issue a pop command to any arg whose index is greater than something.
    @Override
    public void MIPSme() {

    }
}
