package IR;

import MIPS.MIPSER;
import TEMP.*;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class IRcommand_MathOpBase extends IRcommand{
    public enum MathOpType {
        Add(false),
        Sub(false),
        Lt(false),
        Gt(false),
        Eq(false),
        Mul(true),
        Div(true);
        boolean onlyRegs;
        MathOpType(boolean regs) {onlyRegs = regs; }
    }
    TEMP dst, op1, op2;
    static public List<IRcommand> get(MathOpType t, TEMP dst, TEMP op1, TEMP op2, boolean bound) {
        ArrayList<IRcommand> out = new ArrayList<>();
        if (op1 instanceof TEMP_IMMEDIATE) {
            out.add(new IRcommand_Move(dst, op1));
            op1 = dst;
        }
        if (t.onlyRegs) {
            if (op2 instanceof TEMP_IMMEDIATE) {
                TEMP temp = TEMP_FACTORY.getInstance().getFreshTEMP();
                out.add(new IRcommand_Move(temp, op2));
                op2 = temp;
            }
        }
        out.add(emitOp(t, dst, op1, op2, bound));
        return out;
    }

    private static IRcommand emitOp(MathOpType t, TEMP dst, TEMP op1, TEMP op2, boolean bounded) {
        switch (t) {
            case Add:
                return new IRcommand_MathOpBase.Add(dst, op1, op2, bounded);
            case Sub:
                return new IRcommand_MathOpBase.Sub(dst, op1, op2, bounded);
            case Mul:
                return new IRcommand_MathOpBase.Mul(dst, op1, op2, bounded);
            case Div:
                return new IRcommand_MathOpBase.Div(dst, op1, op2, bounded);
            case Lt:
                return new IRcommand_MathOpBase.Lt(dst, op1, op2, bounded);
            case Gt:
                return new IRcommand_MathOpBase.Gt(dst, op1, op2, bounded);
            case Eq:
                return new IRcommand_MathOpBase.Eq(dst, op1, op2, bounded);
        }
        assert false; /// shouldn't happen
        return null;
    }

    //todo: remove bounded from everything
    IRcommand_MathOpBase(TEMP dst, TEMP op1, TEMP op2, boolean bounded) {
        this.dst = dst;
        this.op1 = op1;
        this.op2 = op2;
    }

    @Override
    public List<TEMP> tempsUsed() {
        return Arrays.asList(op1, op2);
    }

    @Override
    public TEMP dst() {
        return dst;
    }

    // actual commands:
    public static class Add extends  IRcommand_MathOpBase {

        public Add(TEMP dst, TEMP op1, TEMP op2, boolean bounded) {
            super(dst, op1, op2, bounded);
        }
        @Override
        public void MIPSme() {
            MIPSER.getInstance().add(dst, op1, op2);
        }
    }

    public static class Sub extends  IRcommand_MathOpBase {

        public Sub(TEMP dst, TEMP op1, TEMP op2, boolean bounded) {
            super(dst, op1, op2, bounded);
        }

        @Override
        public void MIPSme() {
            MIPSER.getInstance().sub(dst, op1, op2);
        }
    }

    public static class Div extends  IRcommand_MathOpBase {

        public Div(TEMP dst, TEMP op1, TEMP op2, boolean bounded) {
            super(dst, op1, op2, bounded);
        }

        @Override
        public void MIPSme() {
            MIPSER.getInstance().beq(op2, REGISTER.zero,
                    TEMP_FACTORY.getInstance().getImmediateTEMP(CRTError.DivideByZero.exitLabelName()));
            MIPSER.getInstance().div(dst, op1, op2);
        }
    }

    public static class Mul extends  IRcommand_MathOpBase {

        public Mul(TEMP dst, TEMP op1, TEMP op2, boolean bounded) {
            super(dst, op1, op2, bounded);
        }

        @Override
        public void MIPSme() {
            MIPSER.getInstance().mul(dst, op1, op2);
        }
    }

    public static class Lt extends IRcommand_MathOpBase {
        public Lt(TEMP dst, TEMP op1, TEMP op2, boolean bounded) {
            super(dst, op1, op2, bounded);
        }

        @Override
        public void MIPSme() {
            MIPSER.getInstance().slt(dst, op1, op2);
        }
    }

    public static class Gt extends IRcommand_MathOpBase {
        public Gt(TEMP dst, TEMP op1, TEMP op2, boolean bounded) {
            super(dst, op1, op2, bounded);
        }

        @Override
        public void MIPSme() {
            MIPSER.getInstance().sgt(dst, op1, op2);
        }
    }

    public static class Eq extends IRcommand_MathOpBase {
        public Eq(TEMP dst, TEMP op1, TEMP op2, boolean bounded) {
            super(dst, op1, op2, bounded);
        }

        @Override
        public void MIPSme() {
            MIPSER.getInstance().seq(dst, op1, op2);
        }
    }
}

