/***********/
/* PACKAGE */
/***********/
package TEMP;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */

/*******************/

public class TEMP_FACTORY
{
	private int counter=0;
	private int labelCount = 0;
	
	public TEMP getFreshTEMP()
	{
		return new TEMP(counter++);
	}
	public TEMP_IMMEDIATE getImmediateTEMP(int value) { return new TEMP_IMMEDIATE.TEMP_INT(value); }
	public TEMP_IMMEDIATE getImmediateTEMP(String value) { return new TEMP_IMMEDIATE.TEMP_LABEL(value); }
	/**************************************/
	/* USUAL SINGLETON IMPLEMENTATION ... */
	/**************************************/
	private static TEMP_FACTORY instance = null;

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected TEMP_FACTORY() {}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	public static TEMP_FACTORY getInstance()
	{
		if (instance == null)
		{
			/*******************************/
			/* [0] The instance itself ... */
			/*******************************/
			instance = new TEMP_FACTORY();
		}
		return instance;
	}

	public TEMP getNilTemp() {
		return REGISTER.zero;
	}

	public TEMP getFalseTEMP() { return REGISTER.zero; }
	public TEMP_IMMEDIATE getFreshLabel(String msg) {
		return new TEMP_IMMEDIATE.TEMP_LABEL(String.format("Label_%d_%s", labelCount++, msg));
	}

}
