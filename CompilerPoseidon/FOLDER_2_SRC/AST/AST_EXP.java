package AST;

public abstract class AST_EXP extends AST_Node
{

	//used for data_members initialization in class decliration
	public boolean is_pure_int() {return false;}
	public boolean is_pure_string() {return false;}
	public boolean is_pure_nil() {return false;}


}
