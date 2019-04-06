package TYPES;

public abstract class TYPE
{
	/******************************/
	/*  Every type has a name ... */
	/******************************/
	public String name;

	/*************/
	/* isClass() */
	/*************/
	public TYPE_CLASS asClass(){ return null;}

	/*************/
	/* asArray() */
	/*************/
	public TYPE_ARRAY asArray(){ return null;}


	/*************/
	/* asList() */
	/*************/
//	public TYPE_LIST asList(){ return null;}
	
	/*************/
	/* asFunction() */
	/*************/
	public TYPE_FUNCTION asFunction(){ return null;}

	/*************/
	/* isNullable() */
	/*************/
	public boolean is_nullable(){return false;}

	/*************/
	/* is_derived_from()
	 * will generally return false, classes override this. */
	/*************/
	public boolean is_derived_from(TYPE t) { return this == t; }

	public boolean canBecome(TYPE t) { return false; }
	
	public String getName() {
		return name;
	}
	/*
	public boolean checkAssigned(TYPE assinged) {
		
		return ((assigned != null) && our_type.canBecome(assinged)) ;

		
	}
	*/
	
	
}
