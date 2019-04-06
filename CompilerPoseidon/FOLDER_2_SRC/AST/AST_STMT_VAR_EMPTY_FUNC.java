package AST;

public class AST_STMT_VAR_EMPTY_FUNC extends AST_STMT_ALL_FUNC_BASE
{
	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_STMT_VAR_EMPTY_FUNC(AST_VAR var, String name)
	{
		super(var, name, null);
	}

	/*********************************************************/
	/* The printing message for a vardec statement AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/********************************************/
		/* AST NODE TYPE = AST NODE STMT VAR EMPTY FUNC */
		/********************************************/
		System.out.format("AST NODE STMT VAR EMPTY FUNC( %s )\n",name);

		/***********************************/
		/* RECURSIVELY PRINT (name and) var ... */
		/***********************************/
		if (var != null) var.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("STMT VAR EMPTY FUNC\n(%s)",name));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,var.SerialNumber);
	}
}
