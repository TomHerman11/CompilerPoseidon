package AST;
import IR.IR;
import TEMP.*;
import TYPES.*;

public class AST_EXP_NIL extends AST_EXP
{

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_NIL()
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.print("====================== exp -> NIL\n");

	}
	
	/***********************************************/
	/* The default message for an exp NIL AST node */
	/***********************************************/
	public void PrintMe()
	{
		/************************************/
		/* AST NODE TYPE = EXP NIL AST NODE */
		/************************************/
		System.out.print("AST NODE EXP NIL\n");

		
		/*********************************/
		/* Print to AST GRAPHIZ DOT file */
		/*********************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"EXP\nNIL");
			
	}
	
	//used for data_members initialization in class decliration
	public boolean is_pure_nil() {return true;}


	public TYPE SemantMe() 
	{
		return TYPE_NIL.getInstance();
	}

	public TEMP IRme() {
		return TEMP_FACTORY.getInstance().getNilTemp();
	}
	
}
