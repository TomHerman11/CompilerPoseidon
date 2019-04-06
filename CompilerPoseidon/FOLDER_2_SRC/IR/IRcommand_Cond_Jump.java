package IR;

import MIPS.MIPSER;
import TEMP.*;

import java.util.Arrays;
import java.util.List;

abstract public class IRcommand_Cond_Jump extends IRcommand {
    TEMP_IMMEDIATE dest;
    TEMP op1;
    TEMP op2;

    public IRcommand_Cond_Jump(TEMP op1, TEMP op2, TEMP_IMMEDIATE dest) {
        this.dest = dest;
        this.op1 = op1;
        this.op2 = op2;
    }

    public String labelName() { return dest.labelName(); }
	
	public String ref_labelName() {
		return labelName();
	}

    @Override
    public List<TEMP> tempsUsed() {
        return Arrays.asList(op1, op2);
    }

    static public class Eq extends IRcommand_Cond_Jump {
        public Eq(TEMP op1, TEMP op2, TEMP_IMMEDIATE dest) {
            super(op1, op2, dest);
        }

        @Override
        public void MIPSme() {
            assert !(op1 instanceof TEMP_IMMEDIATE.TEMP_INT && op2 instanceof TEMP_IMMEDIATE.TEMP_INT);
            if (op1 instanceof TEMP_IMMEDIATE) {
                TEMP sw = op1;
                op1 = op2;
                op2 = sw;
            }
            MIPSER.getInstance().beq(op1, op2 , dest);
        }
    }
    static public class Neq extends IRcommand_Cond_Jump {
        public Neq(TEMP op1, TEMP op2, TEMP_IMMEDIATE dest) {
            super(op1, op2, dest);
        }

        @Override
        public void MIPSme() {
            assert !(op1 instanceof TEMP_IMMEDIATE.TEMP_INT && op2 instanceof TEMP_IMMEDIATE.TEMP_INT);
            if (op1 instanceof TEMP_IMMEDIATE) {
                TEMP sw = op1;
                op1 = op2;
                op2 = sw;
            }MIPSER.getInstance().bne(op1, op2 , dest);
        }
    }

    static public class Ge extends IRcommand_Cond_Jump{
        public Ge(TEMP op1, TEMP op2, TEMP_IMMEDIATE where) {
            super(op1, op2, where);
        }

        @Override
        public void MIPSme() {
            assert !(op1 instanceof TEMP_IMMEDIATE.TEMP_INT && op2 instanceof TEMP_IMMEDIATE.TEMP_INT);
            if (op1 instanceof TEMP_IMMEDIATE) {
                MIPSER.getInstance().ble(op2, op1, dest);
                return;
            }
            MIPSER.getInstance().bge(op1, op2 , dest);
        }
    }

    static public class Lte extends IRcommand_Cond_Jump {
        public Lte(TEMP op1, TEMP op2, TEMP_IMMEDIATE where) {
            super(op1, op2, where);
        }

        @Override
        public void MIPSme() {
            if (op1 instanceof TEMP_IMMEDIATE) {
                MIPSER.getInstance().bge(op2, op1, dest);
                return;
            }
            MIPSER.getInstance().ble(op1, op2 , dest);
        }
    }
}