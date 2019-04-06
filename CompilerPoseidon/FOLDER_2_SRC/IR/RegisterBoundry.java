package IR;

import TEMP.REGISTER;

import java.util.Set;

public interface RegisterBoundry {
    enum BoundryType {
        interFunction,
        local,
        args
    }
    BoundryType boundryType();
    void saveRegisters(Set<REGISTER> names);


}
