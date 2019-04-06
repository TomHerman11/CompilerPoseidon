package TYPES;

public class TYPE_ARRAY extends TYPE
{

	/**************************************************/
	/* THE TYPE OF THE ARRAY OBJECTS*/
	/**************************************************/
	public TYPE data_type;
	
	/****************/
	/* CTROR(S) ... */
	/****************/
	public TYPE_ARRAY(String name, TYPE data_type)
	{
		// if the name is null then we are looking a new expression for array (e.g new int[8]), in the middle of a stmt ('foo(new int[7])' or 'MyIntArr := new int[8]')
		this.name = name;		
		this.data_type = data_type;
	}


	//IT IS AN ARRAY!
	public TYPE_ARRAY asArray(){ return this;}
    
	public boolean is_nullable(){return true;}

	public boolean is_derived_from(TYPE t) { 
		return false;
	}
	/*
	public boolean canBecome(TYPE t) {
		// conditions:
		// 	1. := nil
		//  2. another array of same type
		//  3. new array expression with correct type
		if (t == TYPE_NIL.getInstance()) return true;
		
		TYPE_ARRAY other = t.asArray();
		if (other == null) return false;
		//assuming array type must beq explicit type
		// meaning: array BoogerArray = SomeClass[]
		// 			BoogerArray a = new SomeClass[5]
		return other.data_type == this.data_type;
	}
	*/
	
	
	public boolean canBecome(TYPE t) {
		// conditions:
		// 	1. := nil
		//  2. another array of same type
		//  3. new array expression with correct type
		
		//nil
		if (t == TYPE_NIL.getInstance()) return true;
		
		//check if array
		TYPE_ARRAY other = t.asArray();

		//it is an array!
		if (other != null) {
			//if other.name is null so it's of kind  ":= new Int[4]";
			if (other.name == null ) {
				if (other.data_type == this.data_type) {
					return true;
				}
			}
			
			//other is instance of other "ARRAY" class.
			//must check they both have same name!
			return this.name.equals(other.name);
		}
		
		return false;
	}

}

