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

import java.util.Collections;
import java.util.List;

public class IRcommandPrintInt extends IRcommand
{
	TEMP t;
	
	public IRcommandPrintInt(TEMP t)
	{
		this.t = t;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		MIPSER.getInstance().print_int(t);
	}

	@Override
	public List<TEMP> tempsUsed() {
		return Collections.singletonList(t);
	}
}
