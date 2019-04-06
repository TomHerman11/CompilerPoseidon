package AST;
import TYPES.*;
import SYMBOL_TABLE.*;
import java.util.HashSet;
public abstract class AST_FUNCDEC extends AST_Node
{
	public String name;
	
	public AST_FUNCDEC(String name) {
		
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();
	
		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.name = name;
	}
	
	//semant the function head, and enter him to table
	public TYPE SemantMeSignature(HashSet<String> members_set)throws AST_SEMANTIC_ERROR {return null;} //overrided in AST_FUNCDEC_SIMPLE
	
	//semant the function body(without the head)
	public TYPE SemantMeBody() throws AST_SEMANTIC_ERROR {return null;} //overrided in AST_FUNCDEC_SIMPLE

}
