package AST;
import IR.*;
import TYPES.*;
import TEMP.*;
import SYMBOL_TABLE.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AST_PROGRAM extends AST_Node
{

	public AST_DEC_LIST dec_list;
	List<AST_DEC> everythingElse;
	LinkedList<AST_DEC> fields;
	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_PROGRAM(AST_DEC_LIST dec_list)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();


		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.dec_list=dec_list;
	}

	/*********************************************************/
	/* The printing message for a PROGRAM AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/***********************************/
		/* RECURSIVELY PRINT dec_list ... */
		/***********************************/
		System.out.print("AST NODE PROGRAM");
		if (dec_list != null) dec_list.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"PROGRAM");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,dec_list.SerialNumber);
	}
	
	public TYPE SemantMe() throws AST_SEMANTIC_ERROR
	{
		System.out.println(String.format("%s semantme", this.getClass().getSimpleName()));
		fields = new LinkedList<>();
		everythingElse = new ArrayList<	>();
		dec_list.SemantMe(fields, everythingElse);
		
		/********************************************/
		/* VERIFY THERE IS A "void main()" FUNCTION */
		/********************************************/
		if (SYMBOL_TABLE.getInstance().containsValidMain == false) {
			reportSemanticError(String.format("The program does NOT contain a VALID MAIN function"));
		}
		
		return TYPE_VOID.getInstance();		
	}

	@Override
	public TEMP IRme() {
		// we're in the init stage of the program.
		IR.getInstance().Add_IRcommand(new IRFunctionStart("main"));
		for (AST_DEC f : fields) {
			f.IRme();
		}

		TEMP mainLabel = TEMP_FACTORY.getInstance().getImmediateTEMP("_main");
		TEMP out = TEMP_FACTORY.getInstance().getFreshTEMP();

		// done initialization, call main, then exit.
		IR.getInstance().Add_IRcommand(new IRcommand_Move(REGISTER.fp,REGISTER.zero));
		IR.getInstance().Add_IRcommand(new IRCommand_Call(mainLabel));
		IR.getInstance().Add_IRcommand(new IRcommand_Jump_Label(CRTError.Success.exitLabelName()));
		IR.getInstance().Add_IRcommand(new IRcommand_Exit(out));
		IR.getInstance().Add_IRcommand(new IRcommand_FunctionEnd());
		// done with the init, let's IR all the other things.
		for (AST_DEC f : everythingElse) {
			f.IRme();
		}

		return null;
	}
}
