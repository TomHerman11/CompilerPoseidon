package AST;

public class AST_EXP_EMPTY_FUNC extends AST_EXP_ALL_FUNC_BASE
{

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_EXP_EMPTY_FUNC(String name)
	{
		super(null, name, null);

	}

	/*********************************************************/
	/* The printing message for aN emptyfunc expression AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/********************************************/
		/* AST NODE TYPE = AST EMPTY_FUNC EXPRESSION */
		/********************************************/
		System.out.print("AST NODE EMPTY_FUNC EXPRESSION  \n");

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("EXP EMPTY FUNC\n(%s)", name));
	}
}


