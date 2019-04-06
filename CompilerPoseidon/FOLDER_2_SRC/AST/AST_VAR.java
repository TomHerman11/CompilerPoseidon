package AST;

import IR.IR;
import SYMBOL_TABLE.Variable;
import TEMP.TEMP;
import TEMP.TEMP_FACTORY;

public abstract class AST_VAR extends AST_Node
{
    Variable metadata;

    @Override
    public TEMP IRme() {
        TEMP out = TEMP_FACTORY.getInstance().getFreshTEMP();
        IR.getInstance().Add_IRCommands(metadata.readInto(out));
        return out;
    }

    abstract TEMP IRmeRead();
    abstract TEMP IRmeStore(AST_EXP exp);
}
