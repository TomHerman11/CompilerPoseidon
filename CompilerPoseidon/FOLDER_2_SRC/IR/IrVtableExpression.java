package IR;

import TYPES.TYPE_CLASS;

public class IrVtableExpression extends IrExpression {
    protected TYPE_CLASS content;
    public static String prefix = "vtable_";
    public IrVtableExpression(String name, TYPE_CLASS content) {
        super(name);
        this.content = content;
    }

    public static String fullyQualifiedNameForClassName(String class_name) {
        return prefix + class_name;
    }

    public TYPE_CLASS getContent() { return content; }

    @Override
    public String getNamePrefix() {
        return prefix;
    }

    public String getFullyQualifiedName() { return getNamePrefix() + getName(); }

    @Override
    public String getData() {
        StringBuilder out = new StringBuilder();
        // to create a vtable, we need to write the refs to all the functions in a correct order.
        for (String c : content.fullyQualifiedFunctionNames()) {
            out.append(IR_DATA_SECTION.defineRef(c));
            out.append('\n');
        }
        return out.toString();
    }
}
