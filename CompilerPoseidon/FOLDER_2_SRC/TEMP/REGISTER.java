package TEMP;

import java.util.HashMap;

public class REGISTER extends TEMP {

    private static HashMap<REGISTER_NAME, REGISTER> specials = new HashMap<REGISTER_NAME, REGISTER>();
    final static public REGISTER sp = fromName(REGISTER_NAME.sp);
    final static public REGISTER fp = fromName(REGISTER_NAME.fp);
    final static public REGISTER v0 = fromName(REGISTER_NAME.v0);
    final static public REGISTER ra = fromName(REGISTER_NAME.ra);
    final static public REGISTER args[] = new REGISTER[4];
    final static public REGISTER temps[] = new REGISTER[REGISTER_NAME.TEMP_REGISTER_BOUNDRY.ordinal()];
    final static public REGISTER saved[] = new REGISTER[REGISTER_NAME.TEMP_REGISTER_BOUNDRY.ordinal()];
    final static public REGISTER s0 = saved[0] = fromName(REGISTER_NAME.s0);
    final static public REGISTER s1 = saved[1] =  fromName(REGISTER_NAME.s1);
    public static TEMP zero = fromName(REGISTER_NAME.zero);
    public static REGISTER t0 = temps[0] = fromName(REGISTER_NAME.t0);

    static {
        new REGISTER(REGISTER_NAME.UNASSIGNED); // so that all others will initialize
    }
    private static REGISTER getFrom(REGISTER[] arr, REGISTER_NAME start, int i) {
        if (arr[i] == null) {
            arr[i] = new REGISTER(REGISTER_NAME.values()[start.ordinal() + i]);
        }
        return arr[i];
    }
    public static REGISTER arg(int i) {
        return getFrom(args, REGISTER_NAME.a0, i);
    }
    public static REGISTER temp(int i) {
        return getFrom(temps, REGISTER_NAME.t0, i);
    }
    public static REGISTER saved(int i) {
        return getFrom(saved, REGISTER_NAME.s0, i);
    }
    protected REGISTER(REGISTER_NAME name) {
        super(-1);
        this.register = name;

    }

    public static REGISTER fromName(REGISTER_NAME n) {
        if (n.arg()) return arg(n.ordinal() - REGISTER_NAME.a0.ordinal());
        if (n.temp()) return temp(n.ordinal() - REGISTER_NAME.t0.ordinal());
        if (n.saved()) return saved(n.ordinal() - REGISTER_NAME.s0.ordinal());
        REGISTER out = specials.getOrDefault(n, null);
        if (out == null) {
            out = new REGISTER(n);
            specials.put(n, out);
        }
        return out;

    }
}
