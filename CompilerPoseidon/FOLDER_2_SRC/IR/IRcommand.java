/***********/
/* PACKAGE */
/***********/
package IR;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */

import TEMP.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*******************/

public abstract class IRcommand
{
	/*****************/
	/* Label Factory */
	/*****************/
	protected static String constructorPrefix() { return "ctor_"; }

	protected static int label_counter=0;
	protected TEMP_IMMEDIATE getFreshLabel(String msg)
	{
		return TEMP_FACTORY.getInstance().getFreshLabel(msg);
	}

	/***************/
	/* MIPS me !!! */
	/***************/
	public abstract void MIPSme();

	public List<TEMP> regsUsed() {
		List<TEMP > out = tempsUsed();
		List<TEMP> ret_lst = new ArrayList<TEMP>();
		for (TEMP e : out) {
			if (!(e instanceof TEMP_IMMEDIATE)) {
				ret_lst.add(e);
			}
		}
		
		return ret_lst;
	}
	public List<TEMP> tempsUsed() { return Collections.emptyList(); }
	public TEMP dst() { return null; }

	public boolean hasSideEffects() {
		return false;
	}
	
	public String ref_labelName() {
		return null;
	}

	public String pure_labelName() {
		return null;
	}

	@Override public String toString() {
		ArrayList<String> used = new ArrayList<>(tempsUsed() == null ? 0 : tempsUsed().size());
		for (TEMP e : tempsUsed()) {
			if(e != null) {
				used.add(e.toString()); 
			}
		}
		return String.format("%s %s, %s", this.getClass().getCanonicalName(), dst() == null? "nodst" : dst(), String.join(", ", used));
	}
}
