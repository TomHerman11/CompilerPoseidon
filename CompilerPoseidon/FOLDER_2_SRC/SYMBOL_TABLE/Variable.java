package SYMBOL_TABLE;

import IR.*;
import TEMP.*;
import TYPES.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

abstract public class Variable {
    public String name;
    public TYPE type;
    public int index;

    public Variable(String name, TYPE type, int index) {
        this.name = name;
        this.type = type;
        this.index = index;
    }
    public List<IRcommand> readInto(TEMP out) {return Collections.emptyList();}
    public List<IRcommand> readInto(TEMP out, TEMP storage) {return Collections.emptyList();}
    public List<IRcommand> storeFrom(TEMP value) {return Collections.emptyList();}
    public List<IRcommand> storeFrom(TEMP value, TEMP storage) {return Collections.emptyList();}

    public static Variable findByName(List<Variable> list, String name) {
        for (Variable v: list) {
            if (v.name.equals(name)) return v;
        }
        return null;
    }
}

class LocalVariable extends Variable {
    public LocalVariable(String name, TYPE type, int index) {
    	super(name, type, index);
    }
    public List<IRcommand> readInto(TEMP out) {
        return IRcommand_StackCommand.Read(out, (index + 1));
    }

    public List<IRcommand> storeFrom(TEMP value) {
        return IRcommand_StackCommand.Write(value, (index + 1));
    }

}

class GlobalVariable extends Variable {
    public GlobalVariable(String name, TYPE type) {
        super(IR_DATA_SECTION.getInstance().objectNameFor(name), type, -1); // no index for globals
    }

    public List<IRcommand> readInto(TEMP out) {
        ArrayList<IRcommand> cmds = new ArrayList<IRcommand>();
        TEMP nameTemp = TEMP_FACTORY.getInstance().getImmediateTEMP(name);
        cmds.add(new IRcommand_Load(out, nameTemp));
        return cmds;
    }
    public List<IRcommand> storeFrom(TEMP value) {
        ArrayList<IRcommand> cmds = new ArrayList<IRcommand>();
        TEMP nameTemp = TEMP_FACTORY.getInstance().getImmediateTEMP(name);
        TEMP intermediate = value;
        if (value instanceof TEMP_IMMEDIATE) {
            intermediate = TEMP_FACTORY.getInstance().getFreshTEMP();
            cmds.add(new IRcommand_Move(intermediate, value));
        }
        cmds.add(new IRcommand_Store(nameTemp, intermediate));
        return cmds;
    }
}

class ArgumentVariable extends Variable {
    public ArgumentVariable(String name, TYPE type, int index) {
        super(name, type, index);
        this.index = index;
    }
    public List<IRcommand> readInto(TEMP out) {
        return Collections.singletonList(new IRCommand_Arg.Load(out, index));
    }
    public List<IRcommand> storeFrom(TEMP value) {
        return Collections.singletonList(new IRCommand_Arg.Store(value, index));
    }
}

class ClassVariable extends Variable {
    public ClassVariable(String name, TYPE type, int index) {
        super(name, type, index);
    }

    private List<IRcommand> getStorage(TEMP storage) {
        List<IRcommand> out = new ArrayList<>();
        out.add(new IRcommand_LoadThisArg(storage));
        return out;
    }
    public List<IRcommand> readInto(TEMP into) {
        TEMP storage = TEMP_FACTORY.getInstance().getFreshTEMP();
        List<IRcommand> out = getStorage(storage);
        out.addAll(readInto(into, storage));
        return out;
    }
    public List<IRcommand> readInto(TEMP into, TEMP storage) {
        return Collections.singletonList(new IRcommand_Load(into, storage, index + 1));
    }

    public List<IRcommand> storeFrom(TEMP value) {
        TEMP storage = TEMP_FACTORY.getInstance().getFreshTEMP();
        List<IRcommand> out = getStorage(storage);
        out.addAll(storeFrom(value, storage));
        return out;
    }

    public List<IRcommand> storeFrom(TEMP value, TEMP storage) {
        if (value instanceof TEMP_IMMEDIATE) {
            TEMP intermediate = TEMP_FACTORY.getInstance().getFreshTEMP();
            return Arrays.asList(new IRcommand_Move(intermediate, value),
                                new IRcommand_Store(storage, intermediate, index + 1));

        }
        // this emits sw $value, $storage (+ index...)
        return Collections.singletonList(new IRcommand_Store(storage, value, index + 1));
    }
}


