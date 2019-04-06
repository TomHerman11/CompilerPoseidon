package AST;
import TEMP.TEMP;
import TYPES.*;

import java.util.ArrayList;
import java.util.List;

public abstract class AST_EXP_ALL_FUNC_BASE extends AST_EXP
{
	protected AST_ABSTRACT_FUNC_IMPL impl;

	public String name;
	public AST_VAR var;
	public List<AST_EXP> expList;
	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_EXP_ALL_FUNC_BASE(AST_VAR var, String name, List<AST_EXP> expList)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.expList = expList;
		impl = new AST_ABSTRACT_FUNC_IMPL(var, name, expList);
		this.var = var; // may beq null
		this.name = name; // may NOT beq null
	}

	private void countListWeight() {
		this.nodeWeight = 1;
		if (expList == null) {
			this.nodeWeight = 0;
		}
		else {
			for (AST_EXP e: this.expList) {
				this.nodeWeight += e.nodeWeight;
			}
		}
		impl.nodeWeight = nodeWeight; // set this just in case.
	}

	public TYPE SemantMe() throws AST_SEMANTIC_ERROR {
		impl.setLineNumber(lineNumber);
		TYPE out= impl.SemantMe();
		// count node weight of function call:
		countListWeight();
		return out;
	}

	public TEMP IRme() {
		return impl.IRme();
	}
}
