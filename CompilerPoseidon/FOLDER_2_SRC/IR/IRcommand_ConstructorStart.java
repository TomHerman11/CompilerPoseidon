package IR;

import MIPS.MIPSER;
import TEMP.REGISTER;
import TEMP.TEMP_FACTORY;
import TEMP.TEMP_IMMEDIATE;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class IRcommand_ConstructorStart extends IRFunctionStart implements RegisterBoundry {
    List<REGISTER> saved;
    public IRcommand_ConstructorStart(String class_name) {
        super(class_name);
    }

    @Override
    public String getName() {
        return constructorPrefix() + super.getName();
    }

    @Override
    public BoundryType boundryType() {
        return BoundryType.interFunction;
    }

    @Override
    public void saveRegisters(Set<REGISTER> names) {
        saved = new ArrayList<>(names);
    }

    @Override public void MIPSme() {
        super.MIPSme();
        TEMP_IMMEDIATE size = TEMP_FACTORY.getInstance().getImmediateTEMP(saved.size() * 4);
        MIPSER.getInstance().sub(REGISTER.sp, REGISTER.sp, size);
        MIPSER.getInstance().regsStackStash(saved, true);
    }
}
