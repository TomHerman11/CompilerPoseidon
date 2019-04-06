package AST;
import TYPES.*;
import SYMBOL_TABLE.*;

import java.util.ArrayList;
import java.util.List;

public class AST_EXP_LIST extends AST_Node
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public AST_EXP head;
	public AST_EXP_LIST tail;

	public static List<AST_EXP> toNativeList(AST_EXP_LIST in) {
		List<AST_EXP> out = new ArrayList<>();
		for (AST_EXP_LIST l = in; l != null; l = l.tail) out.add(l.head);
		return out;
	}
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_LIST(AST_EXP head,AST_EXP_LIST tail)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		if (tail != null) System.out.print("====================== EXPs -> EXP EXPs\n");
		if (tail == null) System.out.print("====================== EXPs -> EXP      \n");

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
		System.out.print("AST NODE EXP LIST\n");

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
			"STMT\nLIST\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (head != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,head.SerialNumber);
		if (tail != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,tail.SerialNumber);
	}
	
	//NEED TO CHECK IF USED SOMEWHERE BESIDES FUNCTION CALL- CHANGED THE FUNCTION. DOES NOTHING
	public TYPE SemantMe() {
		assert false;
		return null;
	}
	
	// the only place I've seen this happen is in the middle of a function call, as the argument list.
	/*public TYPE_LIST SemantMeExpList() {
		for(){
			
		}
		
		TYPE mine = head.SemantMe();		
		if (tail != null) {
			TYPE_LIST tail_t = new TYPE_LIST(MINE, tail.SemantMeExpList());
		}
		return new TYPE_LIST(mine, tail == null ? null : tail.asList());
	}*/


}
