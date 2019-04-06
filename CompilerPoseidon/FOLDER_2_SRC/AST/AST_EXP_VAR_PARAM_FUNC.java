package AST;

public class AST_EXP_VAR_PARAM_FUNC extends AST_EXP_ALL_FUNC_BASE
{
	//public AST_VAR var;
	//public String name;
	//public AST_EXP_LIST expList;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_EXP_VAR_PARAM_FUNC( AST_VAR var, String name ,AST_EXP_LIST expList)
	{
		super(var, name, AST_EXP_LIST.toNativeList(expList));
	}

	/*********************************************************/
	/* The printing message for a var.param_func expression AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/********************************************/
		/* AST NODE TYPE = AST VAR.PARAM_FUNC EXPRESSION */
		/********************************************/
		System.out.print("AST NODE VAR.PARAM_FUNC EXPRESSION \n");

		/***********************************/
		/* RECURSIVELY PRINT var + expList ... */
		/***********************************/
		if (var != null) var.PrintMe();
//		if (expList != null) expList.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("EXP VAR PARAM FUNC\n()%S", name));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,var.SerialNumber);
//		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,expList.SerialNumber);
	}
}
