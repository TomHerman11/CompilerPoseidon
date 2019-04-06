package AST;
import TEMP.TEMP;
import TYPES.*;
import SYMBOL_TABLE.*; 
public class AST_DEC_FUNCDEC extends AST_DEC
{
	public AST_FUNCDEC func_dec;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_DEC_FUNCDEC(AST_FUNCDEC func_dec)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.print("====================== dec -> funcdec \n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.func_dec = func_dec;
	}
	
	/*********************************************************/
	/* The printing message for an assign statement AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/********************************************/
		/* AST NODE TYPE = AST ASSIGNMENT STATEMENT */
		/********************************************/
		System.out.print("AST NODE FuncDEC\n");

		/***********************************/
		/* RECURSIVELY PRINT VAR + EXP ... */
		/***********************************/
		if (func_dec != null) func_dec.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"funcdec\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,func_dec.SerialNumber);
	}
	
	//////////////////////////////canBecome/////////////////////////////////////
	
	/***** IF WE GOT HERE - this a global function. semant signature and body together. *****/
	public TYPE SemantMe() throws AST_SEMANTIC_ERROR {
		//this is global function - check if we in global scope:
		if(!SYMBOL_TABLE.getInstance().is_global_scope()) {
			reportSemanticError(String.format("ERROR: function can't beq declared outside of global scope"));
			//exit(0);
			return null;
		}
		
		if (func_dec != null) {
			func_dec.SemantMe();
		}
		
		return TYPE_VOID.getInstance();
	}

	@Override
	public TEMP IRme() { return func_dec.IRme(); }
}
