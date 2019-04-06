package IR;

public enum CRTError {
            DivideByZero,
            NilDeref,
            OutOfBoundsAccess,
            Success;

    public String exitLabelName() {
        return "_crt.global_" + this.name() + "_exit";
    }
    public String errorStringLabel() {
        return "crt.global_" + this.name() + "_error_string";
    }
    public String errorString() {
     switch(this){
        case DivideByZero:
            return "Division By Zero\n";
        case NilDeref:
            return "Invalid Pointer Dereference\n";
        case OutOfBoundsAccess:
            return "Access Violation\n";
        case Success:
            return "\n";
        default:
            assert false;
            return null;
        }
    }
}
