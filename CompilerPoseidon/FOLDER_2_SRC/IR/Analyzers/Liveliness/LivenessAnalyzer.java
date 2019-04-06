package IR.Analyzers.Liveliness;

import IR.Analyzers.FunctionByFunctionAnalyzer;
import IR.IRCommand_Call;
import IR.IRcommand;
import IR.IRcommand_Jump_Label;
import TEMP.TEMP;

import java.util.*;

//TODO: command_db is declared for ONE FUNCTION! the son of the last command_db should beq END and not the next command_db in the file
//TODO: Handle jump commands, KLOMAR do not add current command to the sons of the lastest command.
//TODO: the color/resgister is not passed to anywhere!!!!

/**
 * each LivenessAnalyzer takes a block of code (T.B.D. how and which) and checks the liveness of the temps inside the block.
 * the LivenessAnalyzer is responsible for a bunch of command_db, and runs the liveness algorithm that was taught in class.
 * one block = FUNCTION? THE WHOLE CODE? T.B.D.
 **/
public class LivenessAnalyzer extends FunctionByFunctionAnalyzer {
	//pool of referring commands:
	Map<String, Set<command_db>> label_ref_pool;

	//pool of labels commands.
	Map<String, command_db> pure_label_pool;

	command_db latest_command_db;
	int allowedRegisters;
	//list of the relevant command_db's.
	List<command_db> commands_list=null;
	

	//Constructor:
	public LivenessAnalyzer(int registerCount) {
		latest_command_db = null;
		allowedRegisters = registerCount;
	}

	/*
	@Override
	protected void analyzeFunction(FunctionCFG cfg) {

	}
	*/

	//new command! update all that is necessary.
	public void add_command(List<TEMP> used, TEMP def, String ref_label, String pure_label, boolean is_pure_jump, String cmd_print) {
		//create new command:
		command_db newest_command = new command_db(used, def, cmd_print);
		
		//add the newest command to the list of all commands: 
		commands_list.add(newest_command);
		
		//add the newest command to the sons of the latest command:
		//TODO: handle jump
		if (latest_command_db != null) {
			latest_command_db.sons.add(newest_command);
		}
		
		
		//now newest command is the most recent command:
		if (is_pure_jump) {
			latest_command_db = null;
		}
		else {
			latest_command_db = newest_command;
		}
		
		//add to pool of label_refs this command:
		if (ref_label != null) {
			Set<command_db> ref_label_set = label_ref_pool.get(ref_label);
			
			//if label does not exist in the pool, add it:
			if (ref_label_set == null) {
				ref_label_set = new HashSet<>();
				label_ref_pool.put(ref_label, ref_label_set);
			}

			//add this command to the existing set of commands:

				ref_label_set.add(newest_command);

		}
		
		//add this command to a pool of pure_labels: (if necessary)
		if (pure_label != null) {
			pure_label_pool.put(pure_label, newest_command);
		}
	}
	
	//now take care of the pools: SHIYUH sons to fathers.
	public void addSonsToFathers() {
		//go over pure labels. find all of their fathers:
		Set<String> pure_labels = pure_label_pool.keySet();

		
		for (String son_label : pure_labels) {
			//THIS IS THE SON! the pure label.
			command_db to_be_son = pure_label_pool.get(son_label);

			//now get all future fathers, those who refer to the pure label of the son.
			Set<command_db> future_fathers_lst = label_ref_pool.get(son_label);
			if(future_fathers_lst == null){
				continue;
			}
			for (command_db future_father : future_fathers_lst) {
				//add this pure label to the current father
				future_father.sons.add(to_be_son);
			}
		}
	}

	/*
	private void update_living() {

	}
	*/
	
	/**
	 * the main function of the whole process:
	 * pre: commands_list has all the command
	 * post: all live__out_tag fields are updated
	 */
	public ArrayList<Set<Integer>> calc_liveness() {
		int numCommands = commands_list.size();
		boolean did_not_reach_fix_point = false;
		Set<Integer> tmp_set;
		
		do
		{
			did_not_reach_fix_point = false;
			
			/*******************/
			/* [1] Make a step */
			/*******************/
			
			for (command_db command : commands_list)
			{
			//	psudo: live__in_tag[command] := use[command] U (live_out[command] - def[command]);

				command.live__in_tag = new HashSet<Integer>();
				command.live__in_tag.addAll(command.temp_used);
				
				tmp_set  = new HashSet<Integer>(command.live_out);
				tmp_set.removeAll(command.temp_def);
				command.live__in_tag.addAll(tmp_set);
			}

			/***************************************************************/
			/* [2] Propogate the effect of [1] to related nodes in the CFG */
			/***************************************************************/
			 
			for (command_db c : commands_list)
			{
			//pesudo: live_out_tag[command] := U_{s in successor[n]} live__in_tag[s]

				c.live_out_tag = new HashSet<Integer>();
				for(command_db son : c.sons ){
					c.live_out_tag.addAll(son.live__in_tag);
				}
			}

			/************************************/
			/* [3] check if fix point was found */
			/************************************/
			for (command_db c : commands_list)
			{	if (!c.live__in.equals(c.live__in_tag)) did_not_reach_fix_point = true;
				if (!c.live_out.equals(c.live_out_tag)) did_not_reach_fix_point = true;
			}
			
			/***********************/
			/* [4] sigma := sigma' */
			/***********************/

			for (command_db c : commands_list)
			{
				//pesudo: live__in[command] := live__in_tag[command];
				c.live__in = new HashSet<Integer>(c.live__in_tag); 
				
				//pesudo: live_out[command] := live_out_tag[command];
				c.live_out = new HashSet<Integer>(c.live_out_tag); 
			}
		}
		while (did_not_reach_fix_point);
		
		
		//now prepare the return value. used later in the interference graph:
		ArrayList<Set<Integer>> interferences_list = new ArrayList<Set<Integer>>();
		
		for (command_db curr_command : commands_list) {
			interferences_list.add(curr_command.live_out);
			interferences_list.add(curr_command.live__in);
			interferences_list.add(curr_command.temp_def);
		}
		
		return interferences_list;
	}
	
	/**
	 * calculate the interference graph
	 * (TODO: pass to each TEMP it's register: $t_. currently, each Integer(saved in "Vertex") that represnts a TEMP has color)
	 */
	public void calc_interference_graph(ArrayList<Set<Integer>> interferences_list, int register_count, HashSet<TEMP> tmp_set) {
		interference_graph ig = new interference_graph(register_count);
		ig.color_all_graph(interferences_list, register_count, tmp_set);
	}

	void resetSelf() {
		label_ref_pool = new HashMap<>();
		pure_label_pool = new HashMap<>();
		commands_list = new ArrayList<>();
	}

	/* 
	pre: ir-command list
	post:  commands_list is initialized with deth live and sons */
	@Override
	protected void analyzeFunction(ListIterator<IRcommand> funcStart, ListIterator<IRcommand> end) {
		//System.out.println("-------------------analyzeFunction-------------------");
		
		//resets self restarting the pools of labels
		resetSelf();
		HashSet<TEMP> tmp_set = new HashSet<TEMP>();

		//go over commands
		//System.out.println("");
		//System.out.println("1. go over commands:");
		//int com_no = 1;
		while (funcStart.nextIndex() != end.nextIndex()) {
			//System.out.println("reading command#" + com_no);
			//com_no++;

			IRcommand curr_cmd = funcStart.next();
			
			//if it's IRCommand_call - it's a calling to another function, skip it.
			// CHIBO - I removed this line because it caused us to lose the fact that we might use a reg during the call.
			//if (curr_cmd instanceof IRCommand_Call) continue;

			
			//update the temps that we used

			addCmdToSet(curr_cmd, tmp_set);

			//make a command_db:
			//add_command(List<TEMP> used, TEMP def, String ref_label, String pure_label, boolean is_pure_jump);
			add_command(curr_cmd.regsUsed(), curr_cmd.dst(),
						curr_cmd.ref_labelName(), curr_cmd.pure_labelName(),
						(curr_cmd instanceof IRcommand_Jump_Label),curr_cmd.toString());
		}

		//now the list of commands is almost ready. let's connect between fathers & sons (undirected arrows):
		addSonsToFathers();
		//print_all_commands();
		
		//now calculate liveness:
		//System.out.println("");
		//System.out.println("2. calculate liveness:");
		ArrayList<Set<Integer>> interferences_list = calc_liveness();

		//print_all_commands_expanded();
		//now calculate interference graph:
		//EACH TEMP updated in *calc_interference_graph* to which REGISTER it is allocated
				System.out.println("");
		//System.out.println("3. calculate interference_graph:");
		tmp_set.remove(null);
		calc_interference_graph(interferences_list, this.allowedRegisters, tmp_set);
		
		
		
		//System.out.println("-------------------analyzeFunction - ended-------------------");
	}

	private void addCmdToSet(IRcommand curr_cmd, HashSet<TEMP> tmp_set) {
		tmp_set.addAll(curr_cmd.regsUsed());
		if(curr_cmd.dst() != null){
			tmp_set.add(curr_cmd.dst());
		}
	}


	//------------------//
	//   Printings     //
	//----------------//
	
	@Override public String toString() {
		return "";
	} 
	
	public void print_all_commands(){
		if(commands_list == null){System.out.println("LivenessAnalyzer: command_list is empty"); return;}
		int i=0;
		System.out.println("LivenessAnalyzer: commands are:");
		for(command_db c: commands_list){
			System.out.println(i + ") " + c.get_cmd_name());
			i++;
		}
	}
	
	public void print_all_commands_expanded(){
		if(commands_list == null){System.out.println("LivenessAnalyzer: command_list is empty"); return;}
		int i=0;
		System.out.println("LivenessAnalyzer: commands are:");
		for(command_db c: commands_list){
			System.out.println(c.toString());
			i++;
		}
	}

	
	
}
