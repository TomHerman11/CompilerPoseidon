package AST;
import TYPES.*;
import java.util.HashSet;
public class AST_CFIELD_LIST extends AST_Node
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public AST_CFIELD head;
	public AST_CFIELD_LIST tail;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_CFIELD_LIST(AST_CFIELD head,AST_CFIELD_LIST tail)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		if (tail != null) System.out.print("====================== CFs -> CF CFs\n");
		if (tail == null) System.out.print("====================== CFs -> CF      \n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.head = head;
		this.tail = tail;
	}

	/******************************************************/
	/* The printing message for a statement list AST node */
	/******************************************************/
	public void PrintMe()
	{
		/**************************************/
		/* AST NODE TYPE = AST STATEMENT LIST */
		/**************************************/
		System.out.print("AST NODE CFIELD LIST\n");

		/*************************************/
		/* RECURSIVELY PRINT HEAD + TAIL ... */
		/*************************************/
		if (head != null) head.PrintMe();
		if (tail != null) tail.PrintMe();

		/**********************************/
		/* PRINT to AST GRAPHVIZ DOT file */
		/**********************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"CFIELD\nLIST\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (head != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,head.SerialNumber);
		if (tail != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,tail.SerialNumber);
	}
	
	public TYPE SemantMeVarsAndFunctionSignature(HashSet<String> members_set) throws AST_SEMANTIC_ERROR {
//		if (head != null) head.SemantMeVarsAndFunctionSignature(members_set);
//		if (tail != null) tail.SemantMeVarsAndFunctionSignature(members_set);
//
//		return TYPE_VOID.getInstance();
		assert false; // not used anymore
		return null;
	}
	
	public TYPE SemantMeFunctionBody() throws AST_SEMANTIC_ERROR {
//		if (head != null) head.SemantMeFunctionBody();
//		if (tail != null) tail.SemantMeFunctionBody();
//
//		return TYPE_VOID.getInstance();
		assert false; // this is not used anymore
		return null;
	}
	// ~~~~~~~~ NO IRME NECESSARY, THIS IS NO LONGER USED ~~~~~~~~
	
	
	
	
}
