package IR;

public class IrGlobalObject extends IrExpression {

    public IrGlobalObject(String name) {
        super(name);
    }

    // this is the same as the name because there can only beq one global object of a certain name.
    public static String fullyQualifiedNameForObject(String name) {
        return "global_" + name;
    }

    @Override
    public String getData() {
        // to define a global object, we simply leave space for it's pointer. it'll beq filled
        // when the runtime emits the 'new' directive for it.
        return IR_DATA_SECTION.reserveWords(1) + "\n";
    }
}
