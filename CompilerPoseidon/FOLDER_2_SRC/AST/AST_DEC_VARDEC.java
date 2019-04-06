package AST;
import TEMP.TEMP;
import TYPES.*;
import IR.*;
public class AST_DEC_VARDEC extends AST_DEC
{
	public AST_VARDEC vardec;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_DEC_VARDEC (AST_VARDEC vardec)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.print("====================== dec -> vardec \n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.vardec = vardec;
}
	
	/*********************************************************/
	/* The printing message for an assign statement AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/********************************************/
		/* AST NODE TYPE = AST ASSIGNMENT STATEMENT */
		/********************************************/
		System.out.print("AST NODE VarDEC\n");
		if (vardec != null) vardec.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"VarDec\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,vardec.SerialNumber);
	}	

	public TYPE SemantMe() throws AST_SEMANTIC_ERROR {
		return vardec.SemantMe();
	}

	public TEMP IRme()
	{
		// we know that this is a global variable, because it's a DEC_VARDEC
		// the expression is just the placeholder name to which the data should beq assigned
		IR.getInstance().addData(new IrGlobalObject(vardec.metadata.name));
		return vardec.IRme();
	}
}
