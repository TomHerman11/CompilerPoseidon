package AST;
import TEMP.TEMP;
import TYPES.*;

public class AST_STMT_VARDEC extends AST_STMT
{
	public AST_VARDEC vardec;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_STMT_VARDEC(AST_VARDEC vardec)
	{

		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();


		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.vardec = vardec;

	}

	/*********************************************************/
	/* The printing message for a vardec statement AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/********************************************/
		/* AST NODE TYPE = AST NODE STMT VARDEC */
		/********************************************/
		System.out.print("AST NODE STMT VARDEC\n");

		/***********************************/
		/* RECURSIVELY PRINT vardec ... */
		/***********************************/
		if (vardec != null) vardec.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"STMT VARDEC\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,vardec.SerialNumber);
	}
	
	public TYPE SemantMe() throws AST_SEMANTIC_ERROR {
		vardec.SemantMe();
		return TYPE_VOID.getInstance();
		
	}

	// required in order to set the default value I guess.
	@Override
	public TEMP IRme() {
		return vardec.IRme();
	}
}
