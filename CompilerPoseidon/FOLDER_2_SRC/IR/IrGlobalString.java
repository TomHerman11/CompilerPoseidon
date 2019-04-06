package IR;

public class IrGlobalString extends IrExpression {
    protected String content;
    public IrGlobalString(String name, String content) {
        super(name);
        this.content = content;
    }

    public String getContent() { return content; }

    @Override
    public String emit() {
        return String.join("\n", super.emit(), ".align 2");
    }

    @Override
    public String getData() {
        return IR_DATA_SECTION.define(this.content);
    }
}
