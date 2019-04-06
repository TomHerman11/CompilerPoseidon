package AST;
import TEMP.*;
import TYPES.*;
import IR.*;
import SYMBOL_TABLE.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AST_NEWEXP extends AST_EXP
{
	public String name;
	public AST_EXP exp;
	public TYPE decType; // the type to beq created
	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_NEWEXP(String name, AST_EXP exp)
	{

		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.name = name;
		this.exp = exp;
	}

	/*********************************************************/
	/* The printing message for a NEWEXP AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/***********************************/
		/* RECURSIVELY PRINT (name and) exp ... */
		/***********************************/
		System.out.format("AST NODE NEWEXP( %s )\n",name);
		if (exp != null) exp.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("NEWEXP\n(%s)",name));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (exp != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,exp.SerialNumber);
	}
	
	public TYPE SemantMe() throws AST_SEMANTIC_ERROR {
		// if the exp is not null, then this is an array declaration.
		// if it is an array declaration then the type of the exp should resolve to int.
		//check that name is a legal type (incuding class/ array)
		decType = SYMBOL_TABLE.getInstance().findType(name);
		if (decType == null) {
			reportSemanticError("type not found for new declaration type");
			return null;
		}
		if (exp != null) {
			// haha array.
			TYPE t = exp.SemantMe();
			this.nodeWeight = 1 + this.exp.nodeWeight;
			if (t != TYPE_INT.getInstance()) {
				reportSemanticError(String.format("creating array with non-int (%s) count", t));
				return null;
			}
			return new TYPE_ARRAY(null, decType);
		}
		// not array, can't beq a native type.
		if (!decType.is_nullable()) {
			reportSemanticError("new on native type");
			return null;
		}
		this.nodeWeight = 0; // this is a leaf node.
		return decType;
	}

	private TEMP irArray() {
		TEMP_FACTORY tf = TEMP_FACTORY.getInstance();
		TEMP out = tf.getFreshTEMP();
		TEMP size = exp.IRme();
		TEMP malloc_addr = tf.getImmediateTEMP(CRTSyscall._malloc.name());
		IR irrer = IR.getInstance();
		TEMP intermediateStore = size;
		if (size instanceof TEMP_IMMEDIATE.TEMP_INT) {
			((TEMP_IMMEDIATE.TEMP_INT)size).add(1);
			((TEMP_IMMEDIATE.TEMP_INT)size).mult(4);
			intermediateStore = TEMP_FACTORY.getInstance().getFreshTEMP();
		} else {
			irrer.Add_IRcommand(new IRcommand_Inc(size));
			irrer.Add_IRcommand(
					new IRcommand_MathOpBase.Mul(size, size, tf.getImmediateTEMP(4), false));
		}
		irrer.Add_IRCommands(AST_ABSTRACT_FUNC_IMPL.irActualCallTo(malloc_addr, out, Collections.singletonList(size)));
		// save the size in the array buffer.
		irrer.Add_IRcommand(new IRcommand_Move(intermediateStore, size));
		irrer.Add_IRcommand(new IRcommand_Store(out, intermediateStore));
		return out;
	}

	private TEMP irClass() {
		TEMP_FACTORY fac = TEMP_FACTORY.getInstance();
		TEMP out = fac.getFreshTEMP();
		IR irrer = IR.getInstance();
		TYPE_CLASS cls = decType.asClass();
		assert cls != null;

		// IR a call to malloc
 		TEMP size = fac.getImmediateTEMP(cls.getAllocationWords() * 4);
		TEMP_IMMEDIATE mallocAddr = fac.getImmediateTEMP(CRTSyscall._malloc.name());
		irrer.Add_IRCommands(AST_ABSTRACT_FUNC_IMPL.irActualCallTo(mallocAddr, out, Collections.singletonList(size)));
		// make sure is allocation did not fail:
		irrer.Add_IRcommand(new IRcommand_Cond_Jump.Eq(out, REGISTER.zero,
				fac.getImmediateTEMP(CRTError.NilDeref.exitLabelName())));
		// pass result of malloc to the ctor
		String ctorName = decType.asClass().ctor.fullyQualifiedName();
		TEMP_IMMEDIATE callDest = fac.getImmediateTEMP(ctorName);
		irrer.Add_IRCommands(AST_ABSTRACT_FUNC_IMPL.irActualCallTo(callDest, out, Collections.singletonList(out)));
		return out;
	}

	@Override
	public TEMP IRme() {
		if (exp != null) {
			return irArray();
		}
		return irClass();
	}
}
