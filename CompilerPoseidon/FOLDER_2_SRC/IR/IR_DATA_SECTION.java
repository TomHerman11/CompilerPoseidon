package IR;

import MIPS.MIPSER;
import TEMP.TEMP;
import TEMP.TEMP_FACTORY;
import TEMP.TEMP_IMMEDIATE;
import TYPES.TYPE_FUNCTION;
import TYPES.TYPE_STRING;

import java.util.*;

public class IR_DATA_SECTION extends IRcommand{
    private LinkedHashMap<String, IrExpression> storage;
    static private IR_DATA_SECTION instance;
    private IR_DATA_SECTION() {
        storage = new LinkedHashMap<>();
    }

    public static String define(int i) {
        return String.format(".word %d", i);
    }

    public static String define(String b) {
        return String.format(".asciiz \"%s\"", b);
    }
    public static String defineRef(String name) {
        return String.format(".word %s", name);
    }
    public static String reserveWords(int count) {
        return reserveBytes(count * 4);
    }
    public static String reserveBytes(int count) {
        return String.format(".space %d", count);
    }

    static public IR_DATA_SECTION getInstance() {
        if (instance != null) return instance;
        instance = new IR_DATA_SECTION();
        // declare what should always beq there.
        for (CRTError err : CRTError.values()) {
            instance.addExpression(new IrGlobalString(err.errorStringLabel(), err.errorString()));
        }
        instance.addExpression(TYPE_STRING.EMPTY_STRING);
        return instance;
    }

    public void addExpression(IrExpression exp) {
        IrExpression old = storage.put(exp.fullyQualifiedName(), exp);
        assert old != null && !old.getData().equals(exp.getData()); // should't happen
    }


    public String vtableNameFor(String class_name) {
        // in a better world we'd keep track of all the vtables added and make sure it exists.
        return IrVtableExpression.fullyQualifiedNameForClassName(class_name);
    }

    public String LabelNameForExpression(IrExpression exp) {
        return exp.getName();
    }
    @Override
    public void MIPSme() {
        MIPSER.getInstance().emitData(".data\n");
        for (HashMap.Entry<String,IrExpression> e : this.storage.entrySet()) {
            MIPSER.getInstance().emitData(e.getValue().emit());
        }
        MIPSER.getInstance().emitData(new IrGlobalString(getCrtGlobalSpaceStringName(), " ").emit());
    }

    public String getCrtGlobalSpaceStringName() { return "_crt.space_string"; }
    public String objectNameFor(String name) {
        return IrGlobalObject.fullyQualifiedNameForObject(name);
    }

    public String constructorNameForClassName(String name) {
        return "ctor_" + name;
    }

    public TEMP_IMMEDIATE getGlobalExitLabel() {
        return TEMP_FACTORY.getInstance().getImmediateTEMP("global_exit_label");
    }


    public Map<String, TEMP_IMMEDIATE> getErrorLabelAndStrings() {
        HashMap<String, TEMP_IMMEDIATE> out = new HashMap<>();
        TEMP_FACTORY tf = TEMP_FACTORY.getInstance();
        // todo: add all these to the data section correctly.
        for (CRTError e : CRTError.values()) {
            out.put(e.exitLabelName(), tf.getImmediateTEMP(e.errorStringLabel()));
        }
        return out;
    }
    public String getPrintTraceGlobalName() {return "PrintTrace";}
    public TEMP_IMMEDIATE getFunctionExitLabel(TYPE_FUNCTION containingFunction) {
        return TEMP_FACTORY.getInstance().
                getImmediateTEMP(containingFunction.
                        fullyQualifiedName() + "_cleanup");
    }

    private String getFunctionNameDataLabelString(TYPE_FUNCTION myfunc) {
        return "_crt.funcname_" + myfunc.getName();
    }
    public TEMP_IMMEDIATE getFunctionNameDataLabel(TYPE_FUNCTION myfunc) {
        return TEMP_FACTORY.getInstance().getImmediateTEMP(getFunctionNameDataLabelString(myfunc));
    }

    public void addFunctionNameLabel(TYPE_FUNCTION func) {
        String tracedName = func.getName();
        tracedName = tracedName.equals("_main") ? "main" : tracedName;
        IrExpression string = new IrGlobalString(getFunctionNameDataLabelString(func),tracedName);
        this.addExpression(string);
    }

    public String getJoinStringsGlobalName() {
        return "_crt.join_strings";
    }

    public String getCmpStringsGlobalName() {
        return "_crt.cmp_stings";
    }

    public TEMP_IMMEDIATE getInitFunctionNameFor(String class_name) {
        return TEMP_FACTORY.getInstance().getImmediateTEMP("_crt.init_" + class_name);
    }
}
