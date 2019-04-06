package IR.Analyzers.Liveliness;

import TEMP.TEMP;

import java.util.*;

public class command_db{
	//////////  Fields ////////////////////////
	private String cmd_print; //print the command for debbuging
	Set<Integer> live__in;
	Set<Integer> live_out;
	Set<Integer> live__in_tag;
	Set<Integer> live_out_tag;
	
	//temps used and def/dst:
	Set<Integer> temp_used; //TODO: maybe should we use TEMP?
	Set<Integer> temp_def;
	
	//sons of this commnad, typically a son is found when it's defining a label
	Set<command_db> sons;
	
	//Handle labels: ref_label is the label the command is referring to. i.e.: ("blt Temp_2, Temp_3, Label_76_END") -> ref_label = Label_76_END;


	public command_db(List<TEMP> used, TEMP def, String cmd_print) {

		live__in = new HashSet<Integer>();
		live_out = new HashSet<Integer>();
		live__in_tag = new HashSet<Integer>();
		live_out_tag = new HashSet<Integer>();

		temp_used = new HashSet<>();
		temp_def = new HashSet<>();

		sons = new HashSet<command_db>();
		this.cmd_print = cmd_print;
		for(TEMP tmp : used){
			temp_used.add(tmp.getSerialNumber());
		}

		if(def != null) temp_def.add(def.getSerialNumber());
		temp_used.remove(-1);
		temp_def.remove(-1);
		
	}	
	
	//------------------------//
	//printings for debugging:
	//------------------------//
	
	@Override public String toString() {
		List<String> out = new ArrayList<>();
		out.add("print command: " + cmd_print);
		out.add(setString("live__in", live__in));
		out.add(setString("live_out", live_out));
		out.add(setString("live__in_tag", live__in_tag));
		out.add(setString("live_out_tag", live_out_tag));
		out.add(setString("temp_used", temp_used));
		out.add(setString("temp_def", temp_def));
		out.add("sons:");
		for(command_db s: sons){
			out.add(s.get_cmd_name());
		}
		out.add("---printing command_db completed---");
		return String.join("\n", out);
	} 
	
	private String setString(String setname, Set<Integer> myset){
		return setname + " = " + myset.toString() ;
	}
	
	public String get_cmd_name(){
		return cmd_print;
	}
	
	
}
