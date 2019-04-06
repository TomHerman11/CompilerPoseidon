package AST;
import TEMP.TEMP;
import TYPES.*;
import java.util.HashSet;
public class AST_CFIELD_VARDEC extends AST_CFIELD
{
	/************************/
	/* simple variable name */
	/************************/
	public AST_VARDEC varDec;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_CFIELD_VARDEC(AST_VARDEC varDec)
	{
		
		/*******************************/
		/* COPY INPUT DATA MEMBERS ... */
		/*******************************/
		super(varDec.name);
		this.varDec = varDec;
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();
	
		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.format("====================== cField ->VARDEC\n");


	}


	/*********************************************************/
	/* The printing message for a varDec cField AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/********************************************/
		/* AST NODE TYPE = AST VARDEC CFIELD */
		/********************************************/
		System.out.print("AST NODE VARDEC CFIELD\n");

		/***********************************/
		/* RECURSIVELY PRINT varDec ... */
		/***********************************/
		if (varDec != null) varDec.PrintMe();


		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"VARDEC CFIELD\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,varDec.SerialNumber);
	}
	
	
	public TYPE SemantMeVarsAndFunctionSignature(HashSet<String> members_set) throws AST_SEMANTIC_ERROR {
		members_set.add(name);
		if (varDec != null) {
			return varDec.SemantMe();
		}
		reportSemanticError("vardec null - impossible. check this now.");
		return null;
	}

	@Override
	public void IRConstructor(TEMP obj) {
		// we should already have defined a constructor start.
		// should just emit the line the way it is in the vardec so...
		this.varDec.setThisObject(obj);
		this.varDec.IRme();
	}

}
