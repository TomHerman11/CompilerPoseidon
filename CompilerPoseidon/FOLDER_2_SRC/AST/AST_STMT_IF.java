package AST;
import IR.*;
import TEMP.*;
import TYPES.*;
import SYMBOL_TABLE.*;

public class AST_STMT_IF extends AST_STMT
{
	public AST_EXP cond;
	public AST_STMT_LIST body;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_STMT_IF(AST_EXP cond,AST_STMT_LIST body)
	{

		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();


		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.cond = cond;
		this.body = body;
	}

	/*********************************************************/
	/* The printing message for a if statement AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/********************************************/
		/* AST NODE TYPE = AST IF STATEMENT */
		/********************************************/
		System.out.print("AST NODE IF STMT\n");

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
			"IF STMT\n");
		
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
		System.out.println("IF SEMANT");
		if (cond.SemantMe() != TYPE_INT.getInstance())
		{
			reportSemanticError("ERROR condition inside IF is not int\n");
		}
		
		//get the father scope type:
		TYPE expectedType = SYMBOL_TABLE.getInstance().getScopeType();

		/*************************/
		/* [1] Begin if Scope, the symbol table knows the return type */
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
		/*******************************/
		/* [1] Allocate a fresh label */
		/*******************************/
		TEMP_IMMEDIATE label_end = TEMP_FACTORY.getInstance(). getFreshLabel("end");
	
	
		/********************/
		/* [2] cond.IRme(); */
		/********************/
		TEMP cond_temp = cond.IRme();

		/******************************************/
		/* [3] Jump conditionally to the end */
		/******************************************/
		IR.
		getInstance().
		Add_IRcommand(new IRcommand_Cond_Jump.Eq(cond_temp, TEMP_FACTORY.getInstance().getFalseTEMP(),
				label_end));

		/*******************/
		/* [4] body.IRme() */
		/*******************/
		body.IRme();

		/**********************/
		/* [5] add end label */
		/**********************/
		IR.
		getInstance().
		Add_IRcommand(new IRcommand_Label(label_end));

		/*******************/
		/* [6] return null */
		/*******************/
		return null;
	}
	
		
}
