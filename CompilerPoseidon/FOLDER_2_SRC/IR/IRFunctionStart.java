package IR;

import MIPS.MIPSER;
import TYPES.TYPE_FUNCTION;

public class IRFunctionStart extends IRcommand{
    protected String name;
    public IRFunctionStart(String function_name) {
        this.name = function_name;
    }

    public IRFunctionStart(TYPE_FUNCTION tf) {
        this.name = tf.fullyQualifiedName();
    }
    public String getName() {
        return this.name;
    }
    @Override
    public void MIPSme() {
        // start tracking a function context in the reg name collision algorithm thing
        // note that this works correctly for ctor functions.
    	MIPSER.getInstance().label(getName());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " " + name;
    }
}
