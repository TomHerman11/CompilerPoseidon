package TYPES;

public class TYPE_CLASS_MEMBER_LIST
{
	public TYPE_CLASS_MEMBER head;
	public TYPE_CLASS_MEMBER_LIST tail;
	
	public TYPE_CLASS_MEMBER_LIST(TYPE_CLASS_MEMBER head, TYPE_CLASS_MEMBER_LIST tail)
	{
		this.head = head;
		this.tail = tail;
	}	
}
