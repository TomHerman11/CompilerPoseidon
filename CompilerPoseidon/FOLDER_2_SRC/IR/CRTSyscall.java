package IR;

import java.util.ArrayList;
import java.util.List;

public enum CRTSyscall {
    None(-1),
    PrintInt(1, true),
    PrintString(4),
    _malloc(9),
    exit(10);

    public static List<String> names;
    final int number;
    final boolean printsWhitespace;
    CRTSyscall(int i, boolean prints) { this.number = i; this.printsWhitespace = prints; }
    CRTSyscall(int i) {this(i, false);}

    public static CRTSyscall syscallFor(String name) {
        try {
            return valueOf(name);
        }
        catch(IllegalArgumentException e) {
            return None;
        }
    }

    public int number() {return this.number;}
    public static List<String> allNames() {
        if (names == null) {
            names = new ArrayList<>();
            for (CRTSyscall s : values()) {
                names.add(s.name());
            }
        }
        return names;

    }

    public boolean prints() {
        return this.printsWhitespace;
    }
}
