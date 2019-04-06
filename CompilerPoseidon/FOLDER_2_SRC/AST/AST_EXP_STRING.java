package AST;
import TYPES.*;
import TEMP.*;
import IR.*;

public class AST_EXP_STRING extends AST_EXP
{
	public String s;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_STRING(String s)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.print("====================== exp -> (string)\n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.s = s;
	}
	
	/***********************************************/
	/* The default message for an exp var AST node */
	/***********************************************/
	public void PrintMe()
	{
		/************************************/
		/* AST NODE TYPE = EXP VAR AST NODE */
		/************************************/
		System.out.print("AST NODE EXP STRING\n"+ s );

		
		/*********************************/
		/* Print to AST GRAPHIZ DOT file */
		/*********************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"Exp Stirng\n" + s);
			
	}
	

	//used for data_members initialization in class decliration
	public boolean is_pure_string (){return true;}


	public TYPE SemantMe()
	{
		return TYPE_STRING.getInstance();
				
	}

	/// note: a String is not an object, it's just annotated as such in compile time, it's just a c string pointer.
	@Override
	public TEMP IRme() {
		// we're expecting the string to beq made of spaces only, so there's no issue with tabs etc,
		// and we can make a name using underscore
		IrGlobalString str;
		if(s.length() == 0){
			str = TYPE_STRING.EMPTY_STRING;
		}
		else{
			str = new IrGlobalString("string." + String.join("_", s.split("\\s")), s);
		}
		IR.getInstance().addData( str );
		TEMP out = TEMP_FACTORY.getInstance().getFreshTEMP();
		IR.getInstance().Add_IRcommand(new IRcommand_LoadAddress(out, str.fullyQualifiedName()));
		return out;
	}
}
