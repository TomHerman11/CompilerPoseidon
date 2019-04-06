package TYPES;

public class TYPE_FOR_SCOPE_BOUNDARIES extends TYPE
{
	
	public TYPE return_type;
	
	/****************/
	/* CTROR(S) ... */
	/****************/
	public TYPE_FOR_SCOPE_BOUNDARIES(String name)
	{
		this.name = name;
		this.return_type = TYPE_VOID.getInstance();
	}
	public TYPE_FOR_SCOPE_BOUNDARIES(String name,TYPE return_type)
	{
		this.name = name;
		this.return_type = return_type;
	}
}
