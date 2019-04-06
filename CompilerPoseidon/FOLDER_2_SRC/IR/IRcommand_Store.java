/***********/
/* PACKAGE */
/***********/
package IR;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/
import TEMP.*;
import MIPS.*;

import java.util.Arrays;
import java.util.List;

public class IRcommand_Store extends IRcommand
{
	TEMP where;
	TEMP what;
	int offsetInWords;
	public IRcommand_Store(TEMP where,TEMP what, int offsetInWords)
	{
		this.where = where;
		this.what = what;
		this.offsetInWords = offsetInWords;
	}
	public IRcommand_Store(TEMP where, TEMP what) {
		this(where, what, 0);
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		if (where instanceof TEMP_IMMEDIATE) {
			MIPSER.getInstance().store(what, where);
			return;
		}
		MIPSER.getInstance().store(what, where, 4 * offsetInWords);
	}

	public TEMP dst() {return null;}

	@Override
	public List<TEMP> tempsUsed() {
		return Arrays.asList(what, where);
	}
}
