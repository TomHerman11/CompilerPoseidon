package AST;
import TEMP.TEMP;
import TYPES.*;
import IR.*;
import SYMBOL_TABLE.*;

public class AST_VARDEC_ASSIGN extends AST_VARDEC
{
	public AST_EXP exp; //maybe a null

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_VARDEC_ASSIGN(String type, String name, AST_EXP exp)
	{
		super(type, name);
		this.exp = exp;
	}

	/*********************************************************/
	/* The printing message for a VARDEC ASSIGN AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/***********************************/
		/* RECURSIVELY PRINT (name1 name 2 and) exp ... */
		/***********************************/
		System.out.format("AST NODE VARDEC ASSIGN( %s )\n( %s )",type, name);
		if (exp != null) exp.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("VARDEC ASSIGN\n(%s)\n(%s)",type, name));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,exp.SerialNumber);
	}
	
	private boolean checkAsisgnment() throws AST_SEMANTIC_ERROR {
		TYPE into = SYMBOL_TABLE.getInstance().find(type);
		TYPE from = exp.SemantMe();
		return (into != null) && (from != null) && (into.canBecome(from));
	}

	/**
	FEW RULES FOR DECLARING VARS:
	1. in global - do whatever you want (as long it compiles with AST_VARDEC rules)
	2.1. in class - check that only string or int are initialized (in a specific way!):
		int := 3 V
		int := 3 + 5 X
	2.2. in class - ARRAY/CLASS instance can beq assigned to NIL only.
	**/
	public TYPE SemantMe() throws AST_SEMANTIC_ERROR {
		
		//if assignment is breaking basic rules, checked in AST_VARDEC.
		//if it does - we insert the variable name into the scope! (in super function)
		TYPE type_target = super.SemantMe();

		if (null == type_target) {
			reportSemanticError("wrong variable declaration");
//			System.exit(0);
			return null;
		}

		//check if we are in class DECLARATION of its fields, follow the rules:
		if (SYMBOL_TABLE.getInstance().is_in_class() &&
			SYMBOL_TABLE.getInstance().get_scope_number() == 1)
		{
			//can become will beq checked in next if.
			//let's check if it's pure-int / pure-string / nil
			if (!(	exp.is_pure_int() 		||
					exp.is_pure_string() 	||
					exp.is_pure_nil()		))
			{
				reportSemanticError("wrong variable initialization in class declaration");
	//			System.exit(0);
				return null;
			}
		}

		//if we cannot assign this kind of type.
		if (!checkAsisgnment()) {
			reportSemanticError("variable declaration assignment of bad type");
//			System.exit(0);
			return null;
		}
		this.nodeWeight = 1 + this.exp.nodeWeight;
		return type_target;
	}
	
	public TEMP IRme()
	{
		// the expression cannot beq null, even though it says so in the beginning of the class :)
		assert this.assignee != null;
		this.assignee = exp.IRme();
		return super.IRme();
	}
}
