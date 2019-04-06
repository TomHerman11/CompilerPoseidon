package AST;
import IR.*;
import TEMP.*;
import TYPES.*;

import java.util.ArrayList;
import java.util.List;

public class AST_VAR_SUBSCRIPT extends AST_VAR
{
	public AST_VAR var;
	public AST_EXP subscript;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_VAR_SUBSCRIPT(AST_VAR var,AST_EXP subscript)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.print("====================== var -> var [ exp ]\n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.var = var;
		this.subscript = subscript;
	}

	/*****************************************************/
	/* The printing message for a subscript var AST node */
	/*****************************************************/
	public void PrintMe()
	{
		/*************************************/
		/* AST NODE TYPE = AST SUBSCRIPT VAR */
		/*************************************/
		System.out.print("AST NODE SUBSCRIPT VAR\n");

		/****************************************/
		/* RECURSIVELY PRINT VAR + SUBSRIPT ... */
		/****************************************/
		if (var != null) var.PrintMe();
		if (subscript != null) subscript.PrintMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"SUBSCRIPT\nVAR\n...[...]");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (var       != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,var.SerialNumber);
		if (subscript != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,subscript.SerialNumber);
	}
	
	public TYPE SemantMe() throws AST_SEMANTIC_ERROR {
		TYPE t_var = null;
		TYPE t_exp = null;
		TYPE_ARRAY t_var_array;
		
		/******************************/
		/* [1] Recursively semant var and exp*/
		/******************************/
		if (var != null) t_var = var.SemantMe();
		if (subscript != null) t_exp = subscript.SemantMe();

		
		//CHECK THAT T_EXP IS INT AND NOT ANYTHING ELSE
		if (t_exp != TYPE_INT.getInstance()) {
//			System.out.format(">> ERROR ACCESS ARRAY WITH TYPE WHICH IS NOT INT");
			reportSemanticError("ERROR ACCESS ARRAY WITH TYPE WHICH IS NOT INT");
			return null;
			// System.exit(0);

		}
		
		/*********************************/
		/* [3] Make sure type is a array */
		/*********************************/
		t_var_array = t_var.asArray();
		if (t_var_array == null)
		{
			System.out.format(">> ERROR [%d:%d] access array of a non-array variable\n",6,6);
			reportSemanticError("access %s array of a non-array variable\n");
//			System.exit(0);
			return null;
		}

		return t_var_array.data_type;

	}


	private TEMP checkArrayAccess() {
		IR irrer = IR.getInstance();
		TEMP arrayAddr = var.IRmeRead();
		// now we have the address of the array.
		// we need to make sure that:
		// 1. it is not a null array.
		irrer.Add_IRcommand(new IRcommand_Cond_Jump.Eq(arrayAddr, TEMP_FACTORY.getInstance().getNilTemp(),
				TEMP_FACTORY.getInstance().getImmediateTEMP(CRTError.NilDeref.exitLabelName())));

		return arrayAddr;
	}

	private TEMP checkArrayIndex(TEMP arrayAddr) {
		IR irrer = IR.getInstance();
		// 2. the size is ok:
		TEMP accessIndex = this.subscript.IRme();

		if (accessIndex instanceof TEMP_IMMEDIATE.TEMP_INT) {
			((TEMP_IMMEDIATE.TEMP_INT)accessIndex).mult(4);
			((TEMP_IMMEDIATE.TEMP_INT)accessIndex).add(4);
		}
		else {
			TEMP_IMMEDIATE four = TEMP_FACTORY.getInstance().getImmediateTEMP(4);
			// multiply the index by 4
			irrer.Add_IRcommand(new IRcommand_MathOpBase.Mul(accessIndex, accessIndex, four, false));
			// add the index + 4 to the addr. this takes us to the correct index including the size field.
			irrer.Add_IRcommand(new IRcommand_MathOpBase.Add(accessIndex, accessIndex, four, false));
		}

		TEMP arrSize = TEMP_FACTORY.getInstance().getFreshTEMP();
		irrer.Add_IRcommand(new IRcommand_Load(arrSize, arrayAddr));
		irrer.Add_IRcommand(new IRcommand_Cond_Jump.Ge(accessIndex, arrSize,
				TEMP_FACTORY.getInstance().getImmediateTEMP(CRTError.OutOfBoundsAccess.exitLabelName())));
		return accessIndex;
	}

	// this function generates emits the code that calculates the actual access, and returns the TEMP containing
	// the final address
	private TEMP getAccessAddr(TEMP arrayAddr, TEMP accessIndex) {
		IR irrer = IR.getInstance();
		irrer.Add_IRcommand(new IRcommand_MathOpBase.Add(arrayAddr, accessIndex, arrayAddr, false));

		return arrayAddr;
	}

	private TEMP getAccessAddrChecked() {
		TEMP arrayAddr = checkArrayAccess();
		TEMP accessIndex = checkArrayIndex(arrayAddr);
		return getAccessAddr(arrayAddr, accessIndex);
	}

	@Override
	TEMP IRmeRead() {
		TEMP accessAddr = getAccessAddrChecked();
		TEMP out = TEMP_FACTORY.getInstance().getFreshTEMP();
		IR.getInstance().Add_IRcommand(new IRcommand_Load(out, accessAddr));
		return out;
	}

	@Override
	TEMP IRmeStore(AST_EXP toStore) {
		TEMP accessAddr = getAccessAddrChecked();
		TEMP val = toStore.IRme();
		if (val instanceof TEMP_IMMEDIATE) {
			TEMP intermediate = TEMP_FACTORY.getInstance().getFreshTEMP();
			IR.getInstance().Add_IRcommand(new IRcommand_Move(intermediate, val));
			val = intermediate;
		}
		IR.getInstance().Add_IRcommand(new IRcommand_Store(accessAddr, val));
		return null;
	}
}
