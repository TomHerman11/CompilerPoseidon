package TYPES;

import IR.IrGlobalString;

public class TYPE_STRING extends TYPE
{
	/**************************************/
	/* USUAL SINGLETON IMPLEMENTATION ... */
	/**************************************/
	private static TYPE_STRING instance = null;
	public static IrGlobalString EMPTY_STRING = new IrGlobalString("crt.default_string", "");
	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected TYPE_STRING() {}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	public static TYPE_STRING getInstance()
	{
		if (instance == null)
		{
			instance = new TYPE_STRING();
			instance.name = "string";
		}
		return instance;
	}

	public boolean canBecome(TYPE t) {
		return t == this; // singleton, can only beq same as type.
	}
}
