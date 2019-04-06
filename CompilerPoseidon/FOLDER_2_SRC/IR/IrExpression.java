package IR;

import SYMBOL_TABLE.Variable;

public abstract class IrExpression {
    protected String name;
    private static String nameSep = ":\t";
    public IrExpression(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public String fullyQualifiedName() {
        return getNamePrefix() + getName();
    }
    public String getNamePrefix() {return "";}
    abstract public String getData();
    public String emit() {
        return getNamePrefix() +
                getName() +
                nameSep
                + getData();
    }
}
