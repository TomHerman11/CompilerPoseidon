package AST;
import IR.*;
import TEMP.TEMP;
import TYPES.*;
import SYMBOL_TABLE.*;

public class AST_STMT_RETURN extends AST_STMT
{
	public AST_EXP exp;
	private TYPE_FUNCTION currentFunction;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_STMT_RETURN(AST_EXP exp)
	{

		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();


		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.exp = exp;
	}

	/*********************************************************/
	/* The printing message for a return statement AST node */
	/*********************************************************/
	public void PrintMe() {
		/********************************************/
		/* AST NODE TYPE = AST RETURN STATEMENT */
		/********************************************/
		System.out.print("AST NODE RETURN STMT\n");

		/***********************************/
		/* RECURSIVELY PRINT exp ... */
		/***********************************/
		if (exp != null) exp.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
				SerialNumber,
				"RETURN STMT\n");

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (exp != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, exp.SerialNumber);
	}

	public TYPE SemantMe() throws AST_SEMANTIC_ERROR {
		TYPE myType;
		if (exp == null) {
			myType = TYPE_VOID.getInstance();
		} else {
			myType = exp.SemantMe();
		}
				
		// we're returning TYPE_VOID becuase the return statement on its own has no type,
		// we only need to make sure that the type returned is the same as the type of the function, which we do.
		
		TYPE scope_type  = SYMBOL_TABLE.getInstance().getScopeType();
		//System.out.println("return stmt type: "+myType.name+ " symbol table type: "+scope_type);
		//if(myType.name.equals(scope_type)) return myType;
		//if(scope_type == null)reportSemanticError("scope type is null");
		
		
		/*System.out.println("---------------------------------------------");
		System.out.println("scope type: "+scope_type.name);
		System.out.println("myTpye: "+myType.name);
		System.out.println("---------------------------------------------");
		*/
		
		if (!(myType == TYPE_VOID.getInstance()) && (scope_type == TYPE_VOID.getInstance())) {
			// function return value is not null, we need to check if the statement is correct
			if(!scope_type.canBecome(myType)) {
				reportSemanticError("Return type does not match function");
				// System.exit(0);
				return null;
			}
		}
		currentFunction = SYMBOL_TABLE.getInstance().getCurTypeFunction();
		return myType;
	}

	@Override
	public TEMP IRme() {
		// nothing to do here if the return type is void.
		if (exp != null) {
			// here on out is now a safe place for null pointer exceptions(?)
			TEMP retval = exp.IRme();
			IR.getInstance().Add_IRcommand(new IRCommand_SetRetVal(retval));
		}
		IR.getInstance().Add_IRcommand(new IRcommand_Jump_Label(
				IR_DATA_SECTION.getInstance().
						getFunctionExitLabel(currentFunction).toString()));
		return null;
	}
}
