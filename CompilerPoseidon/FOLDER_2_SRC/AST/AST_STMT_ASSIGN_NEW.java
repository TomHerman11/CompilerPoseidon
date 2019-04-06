package AST;

public class AST_STMT_ASSIGN_NEW extends AST_STMT_ASSIGN
{
	public AST_VAR var;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_STMT_ASSIGN_NEW(AST_VAR var, AST_NEWEXP new_exp)
	{
		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		super(var, new_exp);
		
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();
	}

	/*********************************************************/
	/* The printing message for a ASSIGN NEW statement AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/********************************************/
		/* AST NODE TYPE = AST NODE STMT ASSIGN NEW */
		/********************************************/
		System.out.print("AST NODE STMT ASSIGN NEW\n");


		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
				SerialNumber,
				"STMT ASSIGN NEW\n");


		/***********************************/
		/* RECURSIVELY PRINT vardec ... */
		/***********************************/
		if (var != null) {
			var.PrintMe();
			AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,var.SerialNumber);

		}
		if (exp != null) {
			exp.PrintMe();
			AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,exp.SerialNumber);
		}
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
	}
	// IRme handled in base
}
