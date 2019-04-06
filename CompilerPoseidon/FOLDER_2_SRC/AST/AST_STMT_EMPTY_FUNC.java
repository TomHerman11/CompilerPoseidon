package AST;

public class AST_STMT_EMPTY_FUNC extends AST_STMT_ALL_FUNC_BASE
{
	public String name;


	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_STMT_EMPTY_FUNC(String name)
	{

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		super(null, name, null);

	}

	/*********************************************************/
	/* The printing message for a vardec statement AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/********************************************/
		/* AST NODE TYPE = AST NODE STMT VAR EMPTY FUNC */
		/********************************************/
		System.out.format("AST NODE STMT EMPTY FUNC( %s )\n",name);

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("STMT EMPTY FUNC\n(%s)",name));
	}

	// IRme handled in base
}
