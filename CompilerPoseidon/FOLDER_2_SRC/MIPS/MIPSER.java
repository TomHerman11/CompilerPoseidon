/***********/
/* PACKAGE */
/***********/
package MIPS;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.io.PrintWriter;
import java.util.*;

/*******************/
/* PROJECT IMPORTS */
/*******************/
import IR.CRTSyscall;
import IR.IR_DATA_SECTION;
import IR.RegisterBoundry;
import TEMP.*;
public class MIPSER // a.k.a sir_MIPS_a_lot
{
	public void emitData(String data) {
		programData.add(data);
	}

	public void emitStrlen() {
		label("_crt.strlen");
		// receives one argument, returns cstr len
		move(REGISTER.v0, REGISTER.arg(0));
		move(REGISTER.t0, REGISTER.zero);
		label("_crt.strlen_loop");
		// assuming little endian
		lb(REGISTER.t0, REGISTER.v0, 0);
		beq(REGISTER.t0, REGISTER.zero, TEMP_FACTORY.getInstance().getImmediateTEMP("_crt.strlen_end"));
		add(REGISTER.v0, REGISTER.v0,TEMP_FACTORY.getInstance().getImmediateTEMP(1));
		jump("_crt.strlen_loop");
		label("_crt.strlen_end");
		sub(REGISTER.v0, REGISTER.v0, REGISTER.arg(0));
		jr(REGISTER.ra);
	}

	public void emitJoinStrings(String fname) {
		// function receives 2 arguments, outputs one new string.
		emitStrlen();
		label(fname);
		push(REGISTER.ra);
		push(REGISTER.s0);
		push(REGISTER.s1);

		push(REGISTER.fp);
		move(REGISTER.fp, REGISTER.sp);
		push(REGISTER.arg(0));
		push(REGISTER.arg(1));
		callUnsafe(TEMP_FACTORY.getInstance().getImmediateTEMP("_crt.strlen"));
		move(REGISTER.s0, REGISTER.v0);
		load(REGISTER.arg(0), REGISTER.fp, -8);
		callUnsafe(TEMP_FACTORY.getInstance().getImmediateTEMP("_crt.strlen"));
		move(REGISTER.s1, REGISTER.v0);
		add(REGISTER.arg(0), REGISTER.s1, REGISTER.s0);
		syscallUnsafe(CRTSyscall._malloc);
		// v0 is new string
		move(REGISTER.s0, REGISTER.v0);
		load(REGISTER.arg(0), REGISTER.fp, -4);
		label(fname+"_copy_loop1");
		lb(REGISTER.t0,REGISTER.arg(0),0);
		sb(REGISTER.t0, REGISTER.s0, 0);
		add(REGISTER.arg(0), REGISTER.arg(0), TEMP_FACTORY.getInstance().getImmediateTEMP(1));
		add(REGISTER.s0, REGISTER.s0, TEMP_FACTORY.getInstance().getImmediateTEMP(1));
		bne(REGISTER.t0, REGISTER.zero, TEMP_FACTORY.getInstance().getImmediateTEMP(fname+"_copy_loop1"));
		sub(REGISTER.s0, REGISTER.s0, TEMP_FACTORY.getInstance().getImmediateTEMP(1));
		load(REGISTER.arg(0), REGISTER.fp, -8);
		label(fname+"_copy_loop2");
		lb(REGISTER.t0,REGISTER.arg(0),0);
		sb(REGISTER.t0, REGISTER.s0, 0);
		add(REGISTER.arg(0), REGISTER.arg(0), TEMP_FACTORY.getInstance().getImmediateTEMP(1));
		add(REGISTER.s0, REGISTER.s0, TEMP_FACTORY.getInstance().getImmediateTEMP(1));
		bne(REGISTER.t0, REGISTER.zero, TEMP_FACTORY.getInstance().getImmediateTEMP(fname+"_copy_loop2"));
		// the return value is already in v0
		add(REGISTER.sp, REGISTER.sp, TEMP_FACTORY.getInstance().getImmediateTEMP(8));
		pop(REGISTER.fp);
		pop(REGISTER.s1);
		pop(REGISTER.s0);
		pop(REGISTER.ra);
		jr(REGISTER.ra);
	}

	public void emitStrCmp(String fname) {
		String ok = fname + "ok";
		String next = fname + "next_char";
		String exit = fname + "_exit";
		// t1, t2 current values
		label(fname);
		label(next);

		add(REGISTER.v0, REGISTER.zero, TEMP_FACTORY.getInstance().getImmediateTEMP(1));
		lb(REGISTER.temp(0), REGISTER.arg(0), 0);
		lb(REGISTER.temp(1), REGISTER.arg(1), 0);
		bne(REGISTER.temp(0), REGISTER.temp(1), TEMP_FACTORY.getInstance().getImmediateTEMP(exit));
		beq(REGISTER.temp(0), REGISTER.zero, TEMP_FACTORY.getInstance().getImmediateTEMP(ok));
		inc(REGISTER.arg(0));
		inc(REGISTER.arg(1));
		jump(next);
		label(ok);
		move(REGISTER.v0, REGISTER.zero);
		label(exit);
		jr(REGISTER.ra);
	}

	private void inc(REGISTER arg) {
		add(arg, arg, TEMP_FACTORY.getInstance().getImmediateTEMP(1));
	}

	private void sb(TEMP what, TEMP to, int offset) {
		programLines.format("\tsb %s,%s(%s)\n", what, offset , to);
	}

	public void emitPrintTrace(String fname) {
		label(fname);
		push(REGISTER.s0);
		push(REGISTER.ra);
		move(REGISTER.s0, REGISTER.fp);
		label("_"+fname+"_loop");
		beq(REGISTER.s0, REGISTER.zero, TEMP_FACTORY.getInstance().getImmediateTEMP("_"+fname+"_endloop")); // we set fp to 0 before entering main, so main as next fp as zero
		load(REGISTER.arg(0), REGISTER.s0, 4); // read the function name from the stack frame
		programLines.format("\tli $v0,4");
		programLines.format("\tsyscall");
		load(REGISTER.s0, REGISTER.s0, 0); // read the next fp
		jump("_"+fname+"_loop");
		label("_"+fname+"_endloop");
		pop(REGISTER.ra);
		pop(REGISTER.s0);
		jr(REGISTER.ra);
	}

	public void emitExitConditions(Map<String, TEMP_IMMEDIATE> errors) {
		int i = 0;
		for(String labelName : errors.keySet()) {
			label(labelName);
			load(REGISTER.arg(0), errors.get(labelName),0);
			if (i++ < errors.size()) {
				jump("_global_exit_label");
			}
		}
		label("_global_exit_label");
		syscall(CRTSyscall.PrintString);
		syscall(CRTSyscall.exit);
	}
	public void exit() {
		syscallUnsafe(CRTSyscall.exit);
	}

	public void syscallUnsafe(CRTSyscall type) {
		if (type == CRTSyscall._malloc) {
			REGISTER size = REGISTER.arg(0);
			// make sure size is aligned
			programLines.format("\tsrl %s, %s, %d", size, size, 2);
			add(size, size, TEMP_FACTORY.getInstance().getImmediateTEMP(1));
			programLines.format("\tsll %s, %s, %d", size, size, 2);

		}
		li(REGISTER.v0, TEMP_FACTORY.getInstance().getImmediateTEMP(type.number()));
		programLines.format("\tsyscall");
		if (type.prints()) {
			emit_print_space();
		}
	}

	private void emit_print_space() {
		print_string(TEMP_FACTORY.getInstance().getImmediateTEMP(
				IR_DATA_SECTION.getInstance().getCrtGlobalSpaceStringName()));
	}

	public void syscall(CRTSyscall type) {
//		saveAllArgs(toSave);
		syscallUnsafe(type);
//		restoreAllArgs(toSave);
	}

	public void restoreAllArgs(List<REGISTER> regs) {
		if (regs == null) return;
		assert regs.size() <= TEMP.REGISTER_NAME.TEMP_REGISTER_BOUNDRY.ordinal();
		for (int i = 0 ; i < regs.size(); ++i) {
			move(regs.get(i), REGISTER.saved(i));
		}
	}

	public void saveAllArgs(List<REGISTER> regs) {
		if (regs == null) return;
		assert regs.size() <= TEMP.REGISTER_NAME.TEMP_REGISTER_BOUNDRY.ordinal();
		for (int i = 0 ; i < regs.size(); ++i) {
			move(REGISTER.saved(i), regs.get(i));
		}
	}

	private void getStackRegs(ListIterator<REGISTER> regs) {
		int offset = regs.previousIndex() * 4;
		while (regs.hasPrevious()) {
			load(regs.previous(), REGISTER.sp, offset);
			offset -= 4;
		}
	}

	private void setStackRegs(ListIterator<REGISTER> regs) {
		int offset = 0;
		while (regs.hasNext()) {
			store(regs.next(), REGISTER.sp, offset);
			offset += 4;
		}
	}

	public void regsStackStash(List<REGISTER> regs, boolean push) {
		// this is called after we allocated enough space on the stack for the registers.
		// they come from the top down.
		// otherwise when we want to restore a list we pushed earlier.
		// function pops elemets in the order they were pushed, so (push == false <==> list reversed)
		if (regs == null || regs.size() == 0) return;
		if(push) {
			setStackRegs(regs.listIterator());
		}
		else {
			getStackRegs(regs.listIterator(regs.size()));
		}
	}

	// this function loads ONLY the arguments from the stack,
	// the arguments are saved at the top, so were just using * 4
	public void regsStackLoadArgsOnly(List<REGISTER> regs) {
		for (REGISTER r : regs) {
			int offset = r.register().indexInClass() * 4;
			load(r, REGISTER.sp, offset);
		}
	}

	// this function loads ONLY the arguments from the stack,
	// the arguments are saved at the top, so were just using * 4
	public void regsStackStoreArgsOnly(List<REGISTER> regs) {
		for (REGISTER r : regs) {
			int offset = r.register().indexInClass() * 4;
			store(r, REGISTER.sp, offset);
		}
	}

	public void ble(TEMP op1, TEMP op2, TEMP_IMMEDIATE dest) {
		emitCondJump("ble", op1, op2, dest);
	}

	public void bgt(TEMP op1, TEMP op2, TEMP_IMMEDIATE dest) {
		emitCondJump("bgt", op1, op2, dest);
	}

	public void setOutputFileWriter(PrintWriter writer) {
		instance.fileWriter = writer;
	}

	public void load(TEMP dst, TEMP src) {
		memRelative("lw", dst, src, null);
	}


	public static class MIPSProgram {
		private List<String> lines; 
		public void format(String format, Object... args) {
			add(String.format(format, args));
		}
		public void add(String line) {
			if (lines == null) lines = new ArrayList<>();
			lines.add(line);
		}
		public String toString() {
			if (lines == null) { return ".text\n"; }
			lines.add(0, ".text"); // todo: make sure this is not ".code"
			return String.join("\n", lines);
		}
	}

	private int WORD_SIZE=4;
	private MIPSProgram programLines = new MIPSProgram();
	private List<String> programData = new ArrayList<>();
	/***********************/
	/* The file writer ... */
	/***********************/
	private PrintWriter fileWriter;

	/***********************/
	/* The file writer ... */
	/***********************/
	public void finalizeFile() {
		//programLines.add("\tloadImmediate $v0,10\n");
		//programLines.add("\tsyscall\n"); // todo: why do these lines exist?

		emitExitConditions(IR_DATA_SECTION.getInstance().getErrorLabelAndStrings());
		emitPrintTrace(IR_DATA_SECTION.getInstance().getPrintTraceGlobalName());
		emitJoinStrings(IR_DATA_SECTION.getInstance().getJoinStringsGlobalName());
		emitStrCmp(IR_DATA_SECTION.getInstance().getCmpStringsGlobalName());
		for (String line : programData) {
			fileWriter.format("%s\n", line);
		}
		fileWriter.write(programLines.toString());
				
		//must close the file to flush all writes!!! here is the last use of fileWriter
		fileWriter.close();
	}


	public void print_int(TEMP t)
	{
		programLines.format("\tmove $a0, %s\n", t);
		programLines.format("\tli $v0,1\n");
		programLines.format("\tsyscall\n");
	}

	public void print_string(TEMP t) {
		if (t instanceof TEMP_IMMEDIATE) {
			la(REGISTER.arg(0), t.toString());
		}
		else {
			programLines.format("\tmove $a0, %s\n", t);
		}
		programLines.format("\tli $v0,4\n");
		programLines.format("\tsyscall\n");
	}

	private void emitTrinaryOp(String op, TEMP dst, TEMP op1, TEMP op2) {
		if (op1 instanceof TEMP_IMMEDIATE.TEMP_INT) {
			TEMP t = op2;
			op2 = op1;
			op1 = t;
		}
		programLines.format("\t%s %s,%s,%s", op, dst, op1, op2);
	}

	private void emitCondJump(String op, TEMP op1, TEMP op2, TEMP_IMMEDIATE target) {
		programLines.format("\t%s %s,%s,%s\n", op, op1, op2, target);
	}

    public void move(TEMP into, TEMP what) {
        programLines.format("\tmove %s, %s", into, what);
    }

	public void load(TEMP dst, TEMP src, int offset) {
		if (src instanceof TEMP_IMMEDIATE) {
			String label = ((TEMP_IMMEDIATE) src).labelName();
			if (label != null)la(dst, label);
			else li(dst, (TEMP_IMMEDIATE) src);
		}
		else {
			memRelative("lw", dst, src, TEMP_FACTORY.getInstance().getImmediateTEMP(offset));
		}
	}

	public void lb(TEMP to, TEMP from, int offset) {
		programLines.format("\tlb %s,%s(%s)", to, offset , from);
	}

	public void la(TEMP dst, String label) {
		programLines.format("\tla %s, %s", dst, label);
	}

	public void store(TEMP what,TEMP src, int offset) {
		memRelative("sw", what, src, TEMP_FACTORY.getInstance().getImmediateTEMP(offset));
	}

	public void store(TEMP what,TEMP src) {
		memRelative("sw", what, src, null);
	}

    public void memRelative(String op, TEMP to, TEMP from, TEMP_IMMEDIATE offset) {
		if (offset != null) {
			programLines.format("\t%s %s,%s(%s)", op, to, offset, from);
		}
		else {
			programLines.format("\t%s %s,%s", op, to, from);
		}
	}


	public void loadImmediate(TEMP t, int value) {
		li(t, TEMP_FACTORY.getInstance().getImmediateTEMP(value));
	}

	public void li(TEMP t, TEMP_IMMEDIATE imm) {
		if (imm.toString().equals("0")) {move(t, REGISTER.zero);}
		else {
			programLines.format("\tli %s,%s", t, imm);
		}
	}
	public void label(String inlabel)
	{
		programLines.format("%s:",inlabel);
	}	
	public void jump(String inlabel)
	{
		programLines.format("\tj %s\n",inlabel);
	}

	public void jr(TEMP where) {
		programLines.format("\tjr %s\n",where);
	}

	public void blt(TEMP oprnd1,TEMP oprnd2,TEMP_IMMEDIATE label) {
		emitCondJump("blt", oprnd1, oprnd2, label);
	}
	public void bge(TEMP oprnd1,TEMP oprnd2,TEMP_IMMEDIATE label) {
		emitCondJump("bge", oprnd1, oprnd2, label);
	}

	public void beq(TEMP oprnd1, TEMP oprnd2, TEMP_IMMEDIATE label) {
		emitCondJump("beq", oprnd1, oprnd2, label);
	}

	public void bne(TEMP oprnd1, TEMP oprnd2, TEMP_IMMEDIATE label) {
		emitCondJump("bne", oprnd1, oprnd2, label);
	}
    public void add(TEMP dst,TEMP oprnd1, TEMP oprnd2) 	{
        emitTrinaryOp("add", dst, oprnd1 ,oprnd2);
    }
    public void sub(TEMP dst, TEMP oprnd1, TEMP oprnd2) {
		emitTrinaryOp("sub", dst, oprnd1, oprnd2);
	}
	public void div(TEMP dst, TEMP oprnd1, TEMP oprnd2) {
		emitTrinaryOp("div", dst, oprnd1, oprnd2);
	}
	public void mul(TEMP dst, TEMP oprnd1, TEMP oprnd2) {
		emitTrinaryOp("mul", dst, oprnd1, oprnd2);
	}

    public void seq(TEMP dst, TEMP op1, TEMP op2) {
        emitTrinaryOp("seq", dst, op1, op2);
    }
    public void slt(TEMP dst, TEMP op1, TEMP op2) {
        emitTrinaryOp("slt", dst, op1, op2);
    }
    public void sgt(TEMP dst, TEMP op1, TEMP op2) {
        emitTrinaryOp("sgt", dst, op1, op2);
    }
	public void push(TEMP arg) {
		//todo: assert this is correct
		sub(REGISTER.sp, REGISTER.sp, TEMP_FACTORY.getInstance().getImmediateTEMP(WORD_SIZE));
        store(arg, REGISTER.sp, 0);
	}

	public void pop(TEMP arg) {
		// todo track sp?
		load(arg, REGISTER.sp,0);
        add(REGISTER.sp, REGISTER.sp, TEMP_FACTORY.getInstance().getImmediateTEMP(WORD_SIZE));
	}


	/**************************************/
	/* USUAL SINGLETON IMPLEMENTATION ... */
	/**************************************/
	private static MIPSER instance = null;

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected MIPSER() {}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	public static MIPSER getInstance()
	{
		if (instance != null) return instance;

        /*******************************/
        /* [0] The instance itself ... */
        /*******************************/
        instance = new MIPSER();

        System.out.print("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");

        return instance;
	}

	public void callUnsafe(TEMP target) {
		programLines.format("\tjal %s\n", target);
	}
    public void call(TEMP target) {
//		saveAllArgs(toSave);
        programLines.format("\tjal %s\n", target);
//      restoreAllArgs(toSave);
    }


}
