package AST;

public class AST_EXP_PARAM_FUNC extends AST_EXP_ALL_FUNC_BASE
{
	//public String id;
	public AST_EXP_LIST expListOld;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_EXP_PARAM_FUNC(String name, AST_EXP_LIST expList)
	{
		super(null, name, AST_EXP_LIST.toNativeList(expList));
		this.expListOld = expList;
	}

	/*********************************************************/
	/* The printing message for a param_func expression AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/********************************************/
		/* AST NODE TYPE = AST PARAM_FUNC EXPRESSION */
		/********************************************/
		System.out.print("AST NODE PARAM_FUNC EXPRESSION \n");

		/***********************************/
		/* RECURSIVELY PRINT expList ... */
		/***********************************/
		if (expListOld != null) {
			expListOld.PrintMe();
		}

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("EXP PARAM FUNC\n(%s)", name));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
//		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,expList.SerialNumber);
	}
}
