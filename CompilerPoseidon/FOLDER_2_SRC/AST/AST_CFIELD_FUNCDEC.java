package AST;
import TYPES.*;
import TEMP.TEMP;
import java.util.HashSet;
public class AST_CFIELD_FUNCDEC extends AST_CFIELD
{
	/************************/
	/* simple variable name */
	/************************/
	private AST_FUNCDEC funcDec;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_CFIELD_FUNCDEC(AST_FUNCDEC funcDec)
	{
		super(funcDec.name);
		this.funcDec = funcDec;
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();
	
		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.format("====================== cField ->FUNCDEC\n");

		/*******************************/
		/* COPY INPUT DATA MEMBERS ... */
		/*******************************/

	}


	/*********************************************************/
	/* The printing message for a funcDec cField AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/********************************************/
		/* AST NODE TYPE = AST FUNCDEC CFIELD */
		/********************************************/
		System.out.print("AST NODE FUNCDEC CFIELD\n");

		/***********************************/
		/* RECURSIVELY PRINT funcDec ... */
		/***********************************/
		if (funcDec != null) funcDec.PrintMe();


		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"FUNCDEC CFIELD\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,funcDec.SerialNumber);
	}
	
	///////////////////////////////////////////////////////////////////
	public TYPE SemantMeVarsAndFunctionSignature(HashSet<String> members_set) throws AST_SEMANTIC_ERROR {
		TYPE result = null;
		if (funcDec != null) result = funcDec.SemantMeSignature(members_set);
		return result;
	}
	
	public TYPE SemantMeFunctionBody() throws AST_SEMANTIC_ERROR {
		if (funcDec != null) funcDec.SemantMeBody();
		return TYPE_VOID.getInstance();
	}

	public TEMP IRMethod() {
		return funcDec.IRme();
	}
}
