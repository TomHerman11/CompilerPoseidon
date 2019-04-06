package AST;
import TYPES.*;
import TEMP.*;
import IR.*;


import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public class AST_EXP_BINOP extends AST_EXP
{
	private String OP;
	private BinaryOperator<TYPE> semanter;
	private BinaryOperator<TEMP> ir_op;
	private IRcommand_MathOpBase.MathOpType mathCommandType;
	private AST_EXP left;
	private AST_EXP right;
	private boolean isStringConcat = false; // this changes things for addition.
	private boolean isStringCmp = false;
	private static TYPE semantEq(TYPE t1, TYPE t2) {
		if (t1 == null || t2 == null) {
			return null;
		}
		// int or string? no problem!
		if (t1 == t2 && (t1 == TYPE_STRING.getInstance() || t1 == TYPE_INT.getInstance()))
			return TYPE_INT.getInstance();

		//if first arg is nil and second arg is nil
		if((t1 == TYPE_NIL.getInstance()) && (t2 == TYPE_NIL.getInstance())) return TYPE_INT.getInstance();

		//if first arg is class/array and second is nil
		if((t1.is_nullable()) && (t2 == TYPE_NIL.getInstance())) return TYPE_INT.getInstance();

		//if first arg is nil and second is class/array
		if((t1 == TYPE_NIL.getInstance()) && (t2.is_nullable())) return TYPE_INT.getInstance();

		if (semantMeArray(t1.asArray(), t2.asArray())) return TYPE_INT.getInstance();
		//if first arg is class and second is class:
		if (semantMeClasses(t1.asClass(), t2.asClass())) return TYPE_INT.getInstance();
		return null;
	}
	private static TYPE semantIntOnly(TYPE t1, TYPE t2) {
		if (t1 == TYPE_INT.getInstance() && t2 == TYPE_INT.getInstance()) {
			return TYPE_INT.getInstance();
		}
		return null;
	}
	private static TYPE semantIntString(TYPE t1, TYPE t2) {
		if (null != semantIntOnly(t1, t2))
			return TYPE_INT.getInstance();
		if (t1 == TYPE_STRING.getInstance() && t2 == TYPE_STRING.getInstance())
			return TYPE_STRING.getInstance();

		return null;
	}

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_BINOP(AST_EXP left,AST_EXP right, int OP)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.print("====================== exp -> exp BINOP exp\n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.left = left;
		this.right = right;
		switch (OP) {
			case 0:
				this.OP = "+";
				isStringConcat = true;
				this.semanter = AST_EXP_BINOP::semantIntString;
				mathCommandType = IRcommand_MathOpBase.MathOpType.Add;
				break;
			case 1:
				this.OP = "-";
				this.semanter = AST_EXP_BINOP::semantIntOnly;
				mathCommandType = IRcommand_MathOpBase.MathOpType.Sub;
				break;
			case 2:
				this.OP = "*";
				this.semanter = AST_EXP_BINOP::semantIntOnly;
				mathCommandType = IRcommand_MathOpBase.MathOpType.Mul;
				break;
			case 3:
				this.OP = ":";
				this.semanter = AST_EXP_BINOP::semantIntOnly;
				mathCommandType = IRcommand_MathOpBase.MathOpType.Div;
				break;
			case 4:
				this.OP = ">";
				this.semanter = AST_EXP_BINOP::semantIntOnly;
				mathCommandType = IRcommand_MathOpBase.MathOpType.Gt;
				break;
			case 5:
				this.OP = "<";
				this.semanter = AST_EXP_BINOP::semantIntOnly;
				mathCommandType = IRcommand_MathOpBase.MathOpType.Lt;
				break;
			case 6:
				this.OP = "=";
				isStringCmp = true;
				this.semanter = AST_EXP_BINOP::semantEq;
				mathCommandType = IRcommand_MathOpBase.MathOpType.Eq;
				break;
		}
	}

	/*************************************************/
	/* The printing message for a binop exp AST node */
	/*************************************************/
	public void PrintMe()
	{
		String sOP=OP;

		/*************************************/
		/* AST NODE TYPE = AST SUBSCRIPT VAR */
		/*************************************/
		System.out.print("AST NODE BINOP EXP\n");

		/**************************************/
		/* RECURSIVELY PRINT left + right ... */
		/**************************************/
		if (left != null) left.PrintMe();
		if (right != null) right.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("BINOP(%s)", OP));

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (left  != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,left.SerialNumber);
		if (right != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,right.SerialNumber);
	}

	static private boolean semantMeArray(TYPE_ARRAY t1, TYPE_ARRAY t2) {
		if ((t1 != null) && (null != t2)) {
			//check both of arrays are of the same type
			if(t1.name.equals(t2.name)) return true;
		}
		return false;
	}

	static private boolean semantMeClasses(TYPE_CLASS t1, TYPE_CLASS t2) {
		if (t1 != null && t2 != null) {
		//if same class, or
		//first is son and second is father (or the other way)
			return (t1.is_derived_from(t2) || t2.is_derived_from(t1)) ;
		}
		return false;
	}

	public TYPE SemantMe() throws AST_SEMANTIC_ERROR {
		TYPE t1 = null;
		TYPE t2 = null;

		if (left  != null) t1 = left.SemantMe();
		if (right != null) t2 = right.SemantMe();

		if (t1 == null || t2 == null) {
			return null;
		}
		this.nodeWeight = 1 + left.nodeWeight + right.nodeWeight;
		
		TYPE out = semanter.apply(t1, t2);
		// isStringsConcat could beq true iff we are looking at add, and if the types are strings
		isStringConcat  &= t1 == TYPE_STRING.getInstance();
		isStringCmp &= t1 == TYPE_STRING.getInstance();
		if (out != null) {
			return out;
		}

		//IF WE GOT HERE - ERROR!
		reportSemanticError("Binop semntic error");
		return null;
	}

	private TEMP stringCallCommon(TEMP addr, TEMP s1, TEMP s2) {
		TEMP dst = TEMP_FACTORY.getInstance().getFreshTEMP();
		List<IRcommand> callcmds = AST_ABSTRACT_FUNC_IMPL.irActualCallTo(addr, dst, Arrays.asList(s1, s2));
		IR.getInstance().Add_IRCommands(callcmds);
		return dst;
	}
	private TEMP addCmpStringCall(TEMP s1, TEMP s2) {
		TEMP cmpAddr = TEMP_FACTORY.getInstance().getImmediateTEMP(
				IR_DATA_SECTION.getInstance().getCmpStringsGlobalName()
		);
		return stringCallCommon(cmpAddr, s1, s2);
	}

	private TEMP addJoinStringsCall(TEMP s1, TEMP s2) {
		TEMP joinAddr = TEMP_FACTORY.getInstance().getImmediateTEMP(
				IR_DATA_SECTION.getInstance().getJoinStringsGlobalName()
		);
		return stringCallCommon(joinAddr, s1, s2);

	}
	private TEMP addIntOp(TEMP o1, TEMP o2) {
		IR irrer = IR.getInstance();
		TEMP dst = TEMP_FACTORY.getInstance().getFreshTEMP();
		TEMP minmax = TEMP_FACTORY.getInstance().getFreshTEMP();
		TEMP_IMMEDIATE checkUnderflowLabel = TEMP_FACTORY.getInstance().getFreshLabel("overflow");
		TEMP_IMMEDIATE finishLabel = TEMP_FACTORY.getInstance().getFreshLabel("end");
		TEMP_IMMEDIATE maxint = TEMP_FACTORY.getInstance().getImmediateTEMP(0x7fff);
		TEMP_IMMEDIATE minint = TEMP_FACTORY.getInstance().getImmediateTEMP(0xffff8000); // this works. signed by Vered & Tom
		irrer.Add_IRCommands(IRcommand_MathOpBase.get(this.mathCommandType, dst, o1, o2, true));
		irrer.Add_IRcommand(new IRcommand_Move(minmax, maxint));
		// if dst < minmax: check underflow
		irrer.Add_IRcommand(new IRcommand_Cond_Jump.Ge(minmax, dst, checkUnderflowLabel));
		irrer.Add_IRcommand(new IRcommand_Move(dst, maxint));
		irrer.Add_IRcommand(new IRcommand_Jump_Label(finishLabel.toString()));
	irrer.Add_IRcommand(new IRcommand_Label(checkUnderflowLabel));
		irrer.Add_IRcommand(new IRcommand_Move(minmax, minint));
		// if res > MIN_VALUE, it's already set - finish
		//irrer.Add_IRcommand(new IRcommand_Cond_Jump.Lte(dst, minmax, finishLabel));
		irrer.Add_IRcommand(new IRcommand_Cond_Jump.Ge(dst, minmax, finishLabel));
		irrer.Add_IRcommand(new IRcommand_Move(dst, minint));
		irrer.Add_IRcommand(new IRcommand_Label(finishLabel));
		return dst;
	}
	public TEMP IRme()
	{
		TEMP t1 = null;
		TEMP t2 = null;
		assert left != null && right != null;

		if (left.nodeWeight >= right.nodeWeight) {
			t1 = left.IRme();
			t2 = right.IRme();
		} else {
			// emit the heavier tree first.
			t2 = right.IRme();
			t1 = left.IRme();
		}
		assert t2!= null && t1 != null;
		if (isStringConcat) {
			return addJoinStringsCall(t1, t2);
		}
		else if (isStringCmp) {
			return addCmpStringCall(t1, t2);
		}
		return addIntOp(t1, t2);
	}


}
