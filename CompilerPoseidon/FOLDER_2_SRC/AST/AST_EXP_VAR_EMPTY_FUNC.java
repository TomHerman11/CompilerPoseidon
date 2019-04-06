package AST;

public class AST_EXP_VAR_EMPTY_FUNC extends AST_EXP_ALL_FUNC_BASE
{
	//public AST_VAR var;
	//public String funcName;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_EXP_VAR_EMPTY_FUNC(AST_VAR var, String name)
	{
		super(var, name, null);

	}

	/*********************************************************/
	/* The printing message for a var.empty_func expression AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/********************************************/
		/* AST NODE TYPE = AST VAR.EMPTY_FUNC EXPRESSION */
		/********************************************/
		System.out.print("AST NODE VAR.EMPTY_FUNC EXPRESSION  \n");

		/***********************************/
		/* RECURSIVELY PRINT var+ funcName ... */
		/***********************************/
		if (var != null) var.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("EXP VAR EMPTY FUNC\n(%s)", name));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,var.SerialNumber);
	}
}
