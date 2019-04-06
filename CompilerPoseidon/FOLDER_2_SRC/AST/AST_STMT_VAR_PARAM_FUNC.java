package AST;

public class AST_STMT_VAR_PARAM_FUNC extends AST_STMT_ALL_FUNC_BASE
{

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_STMT_VAR_PARAM_FUNC(AST_VAR var, String name, AST_EXP_LIST exp_list)
	{
		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		super(var, name, AST_EXP_LIST.toNativeList(exp_list));
	}

	/*********************************************************/
	/* The printing message for a ... statement AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/********************************************/
		/* AST NODE TYPE = AST NODE STMT VAR EMPTY FUNC */
		/********************************************/
		System.out.format("AST NODE STMT VAR PARAM FUNC( %s )\n",name);

		/***********************************/
		/* RECURSIVELY PRINT (name and) var + exp_list ... */
		/***********************************/
		if (var != null) var.PrintMe();
		if (exp_list != null) {
			for (AST_EXP l : exp_list) {l.PrintMe(); }
		}

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("STMT VAR PARAM FUNC\n(%s)",name));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,var.SerialNumber);
//		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,exp_list.SerialNumber);
	}
	
	/*
	public void PrintMe()
	{

		System.out.format("AST NODE STMT VAR PARAM FUNC( %s )\n",name);


		if (var != null) var.PrintMe();
		if (exp_list != null) exp_list.PrintMe();


		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("STMT VAR PARAM FUNC\n(%s)",name));

		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,var.SerialNumber);
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,exp_list.SerialNumber);
	}*/
	// IRme in base
	
}
