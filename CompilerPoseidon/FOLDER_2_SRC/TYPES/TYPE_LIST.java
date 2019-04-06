package TYPES;

public class TYPE_LIST
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public TYPE head;
	public TYPE_LIST tail;

	/*************/
	/* asList() */
	/*************/
	public TYPE_LIST asList(){ return this;}


	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public TYPE_LIST(TYPE head,TYPE_LIST tail)
	{
		this.head = head;
		this.tail = tail;
	}

	public TYPE find(String name) {
		TYPE_LIST cur = this;
		// while cur is not null and no the type we want
		while (cur != null && (!cur.head.getName().equals(name))) {
			cur = cur.tail;
		}
		// either null or our sting
		return cur.head;
	}

}
