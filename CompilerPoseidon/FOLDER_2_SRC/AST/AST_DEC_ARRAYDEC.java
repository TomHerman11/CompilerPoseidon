package AST;
import TYPES.*;

public class AST_DEC_ARRAYDEC extends AST_DEC
{
	public AST_ARRAYDEC array_dec;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_DEC_ARRAYDEC(AST_ARRAYDEC array_dec)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.print("====================== dec -> arraydec \n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.array_dec = array_dec;
}
	
	/*********************************************************/
	/* The printing message for an assign statement AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/********************************************/
		/* AST NODE TYPE = AST ASSIGNMENT STATEMENT */
		/********************************************/
		System.out.print("AST NODE ArrayDEC\n");
		if (array_dec != null) array_dec.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"ArrayDec\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,array_dec.SerialNumber);

	}


	public TYPE SemantMe() throws AST_SEMANTIC_ERROR {
		
		System.out.println(String.format("%s semantme", this.getClass().getSimpleName()));
		array_dec.SemantMe();

		return TYPE_VOID.getInstance();
	}
	/* no IRme should beq necessary */
}
