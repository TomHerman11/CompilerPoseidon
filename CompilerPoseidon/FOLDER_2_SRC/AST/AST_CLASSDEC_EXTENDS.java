package AST;
import TYPES.*;
import SYMBOL_TABLE.*;

public class AST_CLASSDEC_EXTENDS extends AST_CLASSDEC
{
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_CLASSDEC_EXTENDS(String class_name, String father_name, AST_CFIELD_LIST l)
	{
		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		
		super(class_name, father_name, l);
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.format("====================== classDec-> extends\n");
	}

	/************************************************/
	/* The printing message for an INT EXP AST node */
	/************************************************/
	public void PrintMe()
	{
		/*******************************/
		/* AST NODE TYPE = AST INT EXP */
		/*******************************/
		System.out.format("AST NODE classDec_extends( %s )\n( %s )",class_name, father_name);
//		if (l != null) l.PrintMe();

		/*********************************/
		/* Print to AST GRAPHIZ DOT file */
		/*********************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("classDec_extends\n(%s)\n(%s)",class_name, father_name));

//		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,l.SerialNumber);
	}
	/* SemantMe() in AST_CLASSDEC */
	/* IRme in AST_CLASSDEC */
}
