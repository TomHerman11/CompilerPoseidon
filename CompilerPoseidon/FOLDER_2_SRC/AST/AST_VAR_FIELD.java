package AST;
import TEMP.*;
import TYPES.*;
import SYMBOL_TABLE.*;
import IR.*;

public class AST_VAR_FIELD extends AST_VAR
{
	public AST_VAR var;
	public String fieldName;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_VAR_FIELD(AST_VAR var,String fieldName)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.format("====================== var -> var DOT ID( %s )\n",fieldName);

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.var = var;
		this.fieldName = fieldName;
	}

	/*************************************************/
	/* The printing message for a field var AST node */
	/*************************************************/
	public void PrintMe()
	{
		/*********************************/
		/* AST NODE TYPE = AST FIELD VAR */
		/*********************************/
		System.out.print("AST NODE FIELD VAR\n");

		/**********************************************/
		/* RECURSIVELY PRINT VAR, then FIELD NAME ... */
		/**********************************************/
		if (var != null) var.PrintMe();
		System.out.format("FIELD NAME( %s )\n",fieldName);

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("FIELD\nVAR\n...->%s",fieldName));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (var != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,var.SerialNumber);
	}
	
	
	public TYPE SemantMe() throws AST_SEMANTIC_ERROR {
		TYPE t = null;
		TYPE_CLASS tc = null;
		
		/******************************/
		/* [1] Recursively semant var */
		/******************************/
		if (var != null) t = var.SemantMe();
		
		/*********************************/
		/* [2] Make sure type is a class */
		/*********************************/
		tc = t.asClass();
		if (tc == null)
		{
			//System.out.format(">> ERROR [%d:%d] access %s field of a non-class variable\n",6,6,fieldName);
			reportSemanticError("access %s field of a non-class variable\n");
			return null;
		}

		//CHECK WHAT TO DO ABOUT FIELD NAME - IT IS CHECKING THAT THE TYPE ALREADY DECLARED, DONT WE NEED TO CHECK IF THE DATA MEMEBER WAS DECLARED IN THIS NAME?? 
		
		/************************************/
		/* [3] Look for fiedlName inside tc */
		/************************************/
		TYPE member_type = tc.findMember(fieldName);
		if(member_type == null) {

			reportSemanticError(String.format("ERROR field %s does not exist in class\n", fieldName));
			return null;
		}

		//FIND THE INDEX OF THI DATA_MEMBER (FOR EXAMPLE, A FUNCTION ON TYPE CLASS THAT: FOR person.ID, ID is the 5th data_member.
		this.metadata = tc.getField(fieldName);

		return member_type;
	}

//	TEMP checkVarValid() {
//		IR.getInstance().Add_IRcommand(
//				new IRcommand_Cond_Jump.Eq(from,
//						TEMP_FACTORY.getInstance().getNilTemp(),
//						IR_DATA_SECTION.getInstance().getGlobalExitLabel())
//		);
//		return from;
//	}

	void checkStorage(TEMP store) {
		IR.getInstance().Add_IRcommand(new IRcommand_Cond_Jump.Eq(REGISTER.zero, store,
				TEMP_FACTORY.getInstance().getImmediateTEMP(CRTError.NilDeref.exitLabelName())));
	}
	@Override
	TEMP IRmeRead() {
		TEMP from = var.IRmeRead();
		checkStorage(from);
		TEMP out = TEMP_FACTORY.getInstance().getFreshTEMP();
		IR.getInstance().Add_IRCommands(this.metadata.readInto(out, from));
		return out;
	}

	@Override
	TEMP IRmeStore(AST_EXP toStore) {
		TEMP storage = var.IRmeRead();
		checkStorage(storage);
		TEMP value = toStore.IRme();
		IR.getInstance().Add_IRCommands(this.metadata.storeFrom(value, storage));
		return null;
	}

}
