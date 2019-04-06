package AST;
import TEMP.TEMP;
import TYPES.*;

import java.util.ArrayList;
import java.util.List;

public abstract class AST_STMT_ALL_FUNC_BASE extends AST_STMT
{
	AST_ABSTRACT_FUNC_IMPL impl;
	public String name;
	public AST_VAR var;
	public List<AST_EXP> exp_list;
	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_STMT_ALL_FUNC_BASE(AST_VAR var, String name, List<AST_EXP>exp_list)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		impl = new AST_ABSTRACT_FUNC_IMPL(var, name, exp_list);
		this.var = var; // may beq null
		this.name = name; // may NOT beq null
		this.exp_list = exp_list; // may beq null, since we inherit from AST_STMT_VAR_PARAM
	}

	public TYPE SemantMe() throws AST_SEMANTIC_ERROR {
		impl.setLineNumber(this.lineNumber);
		return impl.SemantMe();
	}

	public TEMP IRme() {
		return impl.IRme();
	}
}
