package AST;
import TYPES.*;
import SYMBOL_TABLE.*;

public class AST_VARDEC_NEWEXP extends AST_VARDEC_ASSIGN
{

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_VARDEC_NEWEXP(String name1, String name2, AST_NEWEXP new_exp)
	{
		super(name1, name2, new_exp);
	}

	/*********************************************************/
	/* The printing message for a VARDEC ASSIGN AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/***********************************/
		/* RECURSIVELY PRINT (name1 name 2 and) new_exp ... */
		/***********************************/
		System.out.format("AST NODE VARDEC NEWEXP( %s )\n( %s )",type, name);
		if (exp != null) exp.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("VARDEC NEWEXP\n(%s)\n(%s)",type, name));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,exp.SerialNumber);
	}


	public TYPE SemantMe() throws AST_SEMANTIC_ERROR {
		//cannot use NEW declaration in class declaration:
		if (SYMBOL_TABLE.getInstance().is_in_class() &&
			SYMBOL_TABLE.getInstance().get_scope_number() == 1)
		{
			reportSemanticError("wrong variable initialization in class declaration");
//			System.exit(0);
			return null;
		}

		return super.SemantMe();
	}

	// IRme in super class
}
