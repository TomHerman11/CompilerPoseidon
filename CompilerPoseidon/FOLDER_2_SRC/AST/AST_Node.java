package AST;
import TEMP.*;
import TYPES.*;
public abstract class AST_Node
{
	/*******************************************/
	/* The serial number is for debug purposes */
	/* In particular, it can help in creating  */
	/* a graphviz dot format of the AST ...    */
	/*******************************************/
	public int SerialNumber;
	protected int lineNumber;
	protected int nodeWeight = 0; // needed to unpack EXPs correctly. every node is a leaf node unless otherwise stated.

	protected AST_Node() {
	}
	/***********************************************/
	/* The default message for an unknown AST node */
	/***********************************************/
	public void PrintMe()
	{
		System.out.print("AST NODE UNKNOWN\n");
	}
	
	public TYPE SemantMe() throws AST_SEMANTIC_ERROR {
		assert false;
		return null;
	}
	public TEMP IRme()
	{
		assert false;
		return null;
	}
	public void setLineNumber(int num) {
		this.lineNumber = num;
	}
	public int getLineNumber(){ return lineNumber + 1 ; }


	protected void reportSemanticError() throws AST_SEMANTIC_ERROR { reportSemanticError(null);}

	protected void reportSemanticError(String error) throws AST_SEMANTIC_ERROR {
		if (error == null)
			throw new AST_SEMANTIC_ERROR(this.getLineNumber());
		throw new AST_SEMANTIC_ERROR(error, this.getLineNumber());
	}
}
