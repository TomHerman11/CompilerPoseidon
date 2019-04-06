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

public class IRcommand_Load extends IRcommand
{
	TEMP dst;
	TEMP src;
	int offsetInWords;

	public IRcommand_Load(TEMP dst, TEMP src, int offsetInWords)
	{
		this.dst = dst;
		this.src = src;
		this.offsetInWords = offsetInWords;
	}

	public IRcommand_Load(TEMP dst,TEMP src)
	{
		this(dst, src, 0);
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		if (src instanceof TEMP_IMMEDIATE) {
			MIPSER.getInstance().load(dst, src);
		}
		else {
			MIPSER.getInstance().load(dst, src, 4 * offsetInWords);
		}
	}

	public TEMP dst() {return dst;}

	@Override
	public List<TEMP> tempsUsed() {
		return Collections.singletonList(src);
	}
}
