/***********/
/* PACKAGE */
/***********/
package IR;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */

import IR.Analyzers.*;
import IR.Analyzers.Liveliness.*;
import java.util.ArrayList;
import java.util.List;

/*******************/

public class IR
{

	private List<IRcommand> command_list;
	/******************/
	/* Add IR command */
	/******************/
	public void Add_IRcommand(IRcommand cmd)
	{
		command_list.add(cmd);
	}

	public void Add_IRCommands(List<IRcommand> cmds) {
		command_list.addAll(cmds);
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		IR_DATA_SECTION.getInstance().MIPSme();
		for (IRcommand cmd : command_list) {
			cmd.MIPSme();
		}
	}

	/**************************************/
	/* USUAL SINGLETON IMPLEMENTATION ... */
	/**************************************/
	private static IR instance = null;

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected IR(List<IRcommand> list) {
		this.command_list = list;
	}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	public static IR getInstance()
	{
		if (instance == null)
		{
			/*******************************/
			/* [0] The instance itself ... */
			/*******************************/
			ArrayList<IRcommand> command_list = new ArrayList<>();
			instance = new IR(command_list);
		}
		return instance;
	}

	public void addData(IrExpression exp) {
		IR_DATA_SECTION.getInstance().addExpression(exp);
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (IRcommand e : command_list) {
			builder.append(e);
			builder.append("\n");
		}
		return builder.toString();
	}

	public void Analyze() {
		List<AnalyzerBase> analyzers = new ArrayList<>();

		analyzers.add(new LivenessAnalyzer(8));
		analyzers.add(new SavedRegisterAnalyzer());
		// todo: change the 8 to a constant arriving from somewhere.

		for (AnalyzerBase an : analyzers) {
			an.analyze(command_list);
		}
	}
}
