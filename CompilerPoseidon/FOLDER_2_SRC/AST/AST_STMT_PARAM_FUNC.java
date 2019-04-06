package AST;

import java.util.ArrayList;

public class AST_STMT_PARAM_FUNC extends AST_STMT_ALL_FUNC_BASE
{
	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	AST_EXP_LIST expListOld;
	public AST_STMT_PARAM_FUNC(String name, AST_EXP_LIST exp_list)
	{
		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		super(null, name, AST_EXP_LIST.toNativeList(exp_list));
		this.expListOld = exp_list;
	}

	/*********************************************************/
	/* The printing message for a ... statement AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/********************************************/
		/* AST NODE TYPE = AST NODE STMT EMPTY FUNC */
		/********************************************/
		System.out.format("AST NODE STMT PARAM FUNC( %s )\n",name);

		/***********************************/
		/* RECURSIVELY PRINT (name and + exp_list ... */
		/***********************************/
		if (expListOld!= null) {
			expListOld.PrintMe();
			AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,expListOld.SerialNumber);
		}

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("STMT PARAM FUNC\n(%s)",name));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
//		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,exp_list.SerialNumber);
	}
}
