package AST;

import TEMP.*;
import IR.*;

import java.util.ArrayList;
import java.util.List;

public class AST_EXP_CALL extends AST_EXP
{
	private final int paramsSerialNumber;
	/****************/
	/* DATA MEMBERS */
	/****************/
	public String funcName;
	public List<AST_EXP> params;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_CALL(String funcName,AST_EXP_LIST params)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();
		this.params = new ArrayList<>();
		this.funcName = funcName;
		this.paramsSerialNumber = params != null ? params.SerialNumber : -1;
		this.params = AST_EXP_LIST.toNativeList(params);
	}

	public TEMP IRme()
	{
		TEMP t=null;
		// todo: this makes no sense. there can be no one type.
		if (params != null) { t = params.IRme(); }
		// todo: this also makes no sense. why call IRcommandPrintInt?
		IR.getInstance().Add_IRcommand(new IRcommandPrintInt(t));
		
		return null;
	}

	/************************************************************/
	/* The printing message for a function declaration AST node */
	/************************************************************/
	public void PrintMe()
	{
		/*************************************************/
		/* AST NODE TYPE = AST NODE FUNCTION DECLARATION */
		/*************************************************/
		System.out.format("CALL(%s)\nWITH:\n",funcName);

		/***************************************/
		/* RECURSIVELY PRINT params + body ... */
		/***************************************/
		if (params != null) {
			for (AST_EXP p : params) {
				p.PrintMe();
			}
		}
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("CALL(%s)\nWITH",funcName));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,this.paramsSerialNumber);
	}
}
