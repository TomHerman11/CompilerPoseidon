package AST;
import IR.*;
import TEMP.*;
import TYPES.*;
import SYMBOL_TABLE.*;

public class AST_VAR_SIMPLE extends AST_VAR
{
	/************************/
	/* simple variable name */
	/************************/
	public String name;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_VAR_SIMPLE(String name)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();
	
		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.format("====================== var -> ID( %s )\n",name);

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.name = name;
		this.metadata = null;
	}

	/**************************************************/
	/* The printing message for a simple var AST node */
	/**************************************************/
	public void PrintMe()
	{
		/**********************************/
		/* AST NODE TYPE = AST SIMPLE VAR */
		/**********************************/
		System.out.format("AST NODE SIMPLE VAR( %s )\n",name);

		/*********************************/
		/* Print to AST GRAPHIZ DOT file */
		/*********************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("SIMPLE\nVAR\n(%s)",name));
	}
	
	public TYPE SemantMe()  throws AST_SEMANTIC_ERROR  
	{
		/**************************************/
		/* Check That Name DOES exist */
		/**************************************/
		TYPE t = SYMBOL_TABLE.getInstance().find(name);
		if (t == null)
		{
			reportSemanticError(String.format(">> ERROR variable %s DOES NOT exist in symbolTable\n",name));
			return null; // to help ide understand
		}
		
		//check that name is indeed a variable's name and not a type/ function's name
		if(name.equals(t.name)) {
			reportSemanticError(String.format(">> ERROR %s is not a variable (it's a type/ function)\n",name));
		}
		
		//update metadata
		this.metadata = SYMBOL_TABLE.getInstance().findMetadata(name);
		
		return t;
				
	}

	@Override
	TEMP IRmeRead() {
		TEMP out = TEMP_FACTORY.getInstance().getFreshTEMP();
		IR.getInstance().Add_IRCommands(metadata.readInto(out));
		return out;}

	@Override
	TEMP IRmeStore(AST_EXP toStore) {
		IR.getInstance().Add_IRCommands(metadata.storeFrom(toStore.IRme()));
		return null;
	}
}
