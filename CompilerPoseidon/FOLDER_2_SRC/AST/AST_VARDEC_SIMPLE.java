package AST;

public class AST_VARDEC_SIMPLE extends AST_VARDEC
{

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_VARDEC_SIMPLE(String name1, String name2) {
		super(name1, name2);
	}

	/*********************************************************/
	/* The printing message for a VARDEC SIMPLE AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/***********************************/
		/* RECURSIVELY PRINT (name1 name 2) ... */
		/***********************************/
		System.out.format("AST NODE VARDEC SIMPLE( %s )\n( %s )",type, name);

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("VARDEC SIMPLE\n(%s)\n(%s)",type, name));

	}
}
