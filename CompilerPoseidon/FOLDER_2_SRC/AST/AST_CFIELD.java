package AST;
import TEMP.TEMP;
import TYPES.*;
import java.util.HashSet;
public abstract class AST_CFIELD extends AST_Node
{
	public String name; // this is the variable/function name
	
	public AST_CFIELD(String name) {
		
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();
	
		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.name = name;
	}
	
	public TYPE SemantMeVarsAndFunctionSignature(HashSet<String> members_set) throws AST_SEMANTIC_ERROR {
		return TYPE_VOID.getInstance();
	}
	
	public TYPE SemantMeFunctionBody() throws AST_SEMANTIC_ERROR {
		return TYPE_VOID.getInstance();  // used in AST_CFIELD_VARDEC. if null is eror, we prefer to return TYPE_VOID
	}

	// this should do something for the data fields in the class.
	void IRConstructor(TEMP obj) { }
	public TEMP IRMethod() { return null; }
}
