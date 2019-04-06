package AST;
import IR.IR_OBJECT_MODEL;
import TEMP.TEMP;
import TYPES.*;

public class AST_DEC_CLASSDEC extends AST_DEC
{
	public AST_CLASSDEC cd;
	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_DEC_CLASSDEC(AST_CLASSDEC cd)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.print("====================== dec -> classdec \n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.cd = cd;
	}
	
	/*********************************************************/
	/* The printing message for an assign statement AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/********************************************/
		/* AST NODE TYPE = AST ASSIGNMENT STATEMENT */
		/********************************************/
		System.out.print("AST NODE Class Dec\n");
		if (cd != null) cd.PrintMe();



		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"classdec\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,cd.SerialNumber);
	}
	
	
	public TYPE SemantMe() throws AST_SEMANTIC_ERROR {
		cd.SemantMe();

		return TYPE_VOID.getInstance();
	}

	@Override
	public TEMP IRme() {
		return cd.IRme();
	}
}
