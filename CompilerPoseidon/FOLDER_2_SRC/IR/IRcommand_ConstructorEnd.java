package IR;

import MIPS.MIPSER;
import TEMP.REGISTER;
import TEMP.TEMP_FACTORY;
import TEMP.TEMP_IMMEDIATE;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class IRcommand_ConstructorEnd extends IRcommand_FunctionEnd implements RegisterBoundry{
    List<REGISTER> saved;

    @Override
    public BoundryType boundryType() {
        return BoundryType.interFunction;
    }

    @Override
    public void saveRegisters(Set<REGISTER> names) {
        saved = new ArrayList<>(names);
    }
    public void MIPSme() {
        TEMP_IMMEDIATE size = TEMP_FACTORY.getInstance().getImmediateTEMP(saved.size() * 4);
        MIPSER.getInstance().regsStackStash(saved, false);
        MIPSER.getInstance().add(REGISTER.sp, REGISTER.sp, size);
        super.MIPSme();
    }
}
