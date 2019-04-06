package AST;
import TYPES.*;
import SYMBOL_TABLE.*;

public class AST_CLASSDEC_SIMPLE extends AST_CLASSDEC
{
	//public String class_name;
	//public AST_CFIELD_LIST l;	

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_CLASSDEC_SIMPLE(String class_name ,AST_CFIELD_LIST l)
	{
		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		super(class_name, null, l);
		
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
		System.out.format("AST NODE classDec_simple\n" + class_name);
//		if (l != null) l.PrintMe();


		/*********************************/
		/* Print to AST GRAPHIZ DOT file */
		/*********************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("classDec_simple\n" + class_name));

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
//		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,l.SerialNumber);
	}
	
	/* SemantMe() in ast_classdec */
	
}
