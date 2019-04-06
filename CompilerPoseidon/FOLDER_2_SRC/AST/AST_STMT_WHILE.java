package AST;

import IR.*;
import TEMP.*;
import TYPES.*;
import SYMBOL_TABLE.*;

public class AST_STMT_WHILE extends AST_STMT
{
	private AST_EXP cond;
	private AST_STMT_LIST body;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	// keeping this as an STMT_LIST because it's important for the AST traversal. no other reason
	public AST_STMT_WHILE(AST_EXP cond,AST_STMT_LIST body)
	{
		this.cond = cond;
		this.body = body;
	
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();
	}

	/*********************************************************/
	/* The printing message for a while statement AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/********************************************/
		/* AST NODE TYPE = AST WHILE STATEMENT */
		/********************************************/
		System.out.print("AST NODE WHILE STMT\n");

		/***********************************/
		/* RECURSIVELY PRINT cond + body ... */
		/***********************************/
		if (cond != null) cond.PrintMe();
		if (body != null) body.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"WHILE STMT\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,cond.SerialNumber);
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,body.SerialNumber);
	}

	
	
	public TYPE SemantMe() throws AST_SEMANTIC_ERROR {
		/****************************/
		/* [0] Semant the Condition */
		/****************************/
		if (cond.SemantMe() != TYPE_INT.getInstance())
		{
			reportSemanticError("ERROR condition inside WHILE is not int");
		}

		/*************************/
		/* [1] Begin if Scope, the symbol table knows who the father type is  */
		/*************************/
		SYMBOL_TABLE.getInstance().beginScope();

		/***************************/
		/* [2] Semant Data Members */
		/***************************/
		//out type:	1) if same as expectedType -->ok
		//			2) if void-->ok
		//			3) if != expectedType,void-->error(handle at the body)		
		TYPE bodyType = body.SemantMe();

		/*****************/
		/* [3] End Scope */
		/*****************/
		SYMBOL_TABLE.getInstance().endScope();

		/*********************************************************/
		/* [4] Return value  */
		/*********************************************************/
		return bodyType;
	}	
	
	public TEMP IRme()
	{
		TEMP_FACTORY tf = TEMP_FACTORY.getInstance();
		/*******************************/
		/* [1] Allocate 2 fresh labels */
		/*******************************/
		TEMP_IMMEDIATE label_start = tf.getFreshLabel("check");
		TEMP_IMMEDIATE label_end = tf.getFreshLabel("end");

		/*********************************/
		/* [2] entry label for the while */
		/*********************************/
		IR.getInstance().Add_IRcommand(new IRcommand_Label(label_start));

		/********************/
		/* [3] cond.IRme(); */
		/********************/
		TEMP cond_temp = cond.IRme();

		/******************************************/
		/* [4] Jump conditionally to the loop end */
		/******************************************/
		IR.getInstance().Add_IRcommand(new IRcommand_Cond_Jump.Eq(cond_temp, tf.getFalseTEMP(), label_end));

		/*******************/
		/* [5] body.IRme() */
		/*******************/
		body.IRme();

		/******************************/
		/* [6] Jump to the loop entry */
		/******************************/
		IR.
		getInstance().
		Add_IRcommand(new IRcommand_Jump_Label(label_start.toString()));

		/**********************/
		/* [7] Loop end label */
		/**********************/
		IR.
		getInstance().
		Add_IRcommand(new IRcommand_Label(label_end));

		/*******************/
		/* [8] return null */
		/*******************/
		return null;
	}

}
