package IR.Analyzers.Liveliness;

import TEMP.TEMP;

import java.util.*;


public class interference_graph {
	final int REGISTERS_AMOUNT;
	Set<Vertex> V;  //Graph representaion as vertex list
	Stack<Vertex> S;
	HashMap<Integer,Integer> temps_colors; // <temp number, color>
	
	//constructor:
	interference_graph(int register_count){
		V = new HashSet<>();
		S = new Stack<>();
		temps_colors = new HashMap <> ();
		REGISTERS_AMOUNT = register_count;
	}
	
	//Inner class
	class Vertex {
		int temp_no;
		int color;
		Set<Vertex> edges;
		
		public Vertex(int temp_no){
			this.temp_no = temp_no;
			this.color = -1;
			this.edges = new HashSet<Vertex>();
		}
		
		public void print(){
			System.out.println("");
			System.out.println("v" + this.temp_no +": color-" + this.color);
			print_integer_set("edges", edges);
		}
	
		public void print_integer_set(String setname, Set<Vertex> neighbors){
			System.out.print(setname + " = {");
			for(Vertex v: neighbors)
				System.out.print(v.temp_no + ",");
			System.out.println("}");	
		}
	}
	
	
	//make the magic happen:
	//input: a list of sets. each set contains integers and is a "live in"
	public void color_all_graph(ArrayList<Set<Integer>> interferences_list, int register_count, HashSet<TEMP> tmp_set){
		//System.out.println("---color_all_graph---");

		//step1: insert vertices to graph + edges
		//System.out.println("step1: insert vertices to graph + edges");

		this.compute_interference_graph(interferences_list);
		//if no Temps detected
		//if(V.size()==0){return;}
		
		//step 2: move vertices to stack
		//System.out.println("V size3 " + V.size());

		//System.out.println("step 2: move vertices to stack");
		while(!this.V.isEmpty())
			this.move_vertex_to_stack();
		//System.out.println("V size4 " + V.size());

		//step 3: pop vertices and paint them 
		//System.out.println("step 3: pop vertices and paint them");	
		while(!this.S.isEmpty())	{
			Vertex temp_v = this.S.pop();
			//System.out.println("popped tmp no: " + temp_v.temp_no);
			this.paint_vertex(temp_v);
		}
		//this.print_graph_lamelly();

		//step 4: paint TEMPS!
		//System.out.println("step 4: paint TEMPS!");
		int color,temp_no;


		if(tmp_set == null){
			//System.out.println("tmp_set = null");
		}
		//System.out.println("temp set size: " +tmp_set.size() );

		//for each temp:
		for(TEMP t: tmp_set){
			if(t.getSerialNumber() == -1){
				continue;
			}
			temp_no = t.getSerialNumber();
			color = temps_colors.get(temp_no);
			t.setRegister(color);

		}
		//System.out.println("---color_all_graph - finished---");

	}
	
	//step 1:
	public void compute_interference_graph(ArrayList<Set<Integer>> interferences_list) {
		Vertex vertex;			// temporary vertex
		Vertex neighbor;

		for(Set<Integer> s : interferences_list){
			if(s == null){ continue;}
			for(Integer i1 : s){

				if(i1 == null) {
					continue;
				}
				vertex = get_vertex(i1);
				
				for(Integer i2 : s){
					if(!Objects.equals(i1, i2)){
						neighbor = get_vertex(i2);
						vertex.edges.add(neighbor);
					}
				}

			}		
		}
	}
	
	public Vertex get_vertex(Integer i1){
		for(Vertex v : V){
			if(v.temp_no == i1){
				return v;
			}
		}
		Vertex v = new Vertex(i1);
		V.add(v);
		return v;
	}
	
	
	//step2: choose_vertex_to_stack(not a lot of neigbors)
	public void move_vertex_to_stack(){
		//System.out.println("V size:" + V.size());
		for(Vertex v : V){
			// ASSUMPTION: NO SPILLS!

			if(v.edges.size() < REGISTERS_AMOUNT){
				S.push(v);
				
				V.remove(v);
				//each neigbore will forget the pushed vertex
				for(Vertex neigbore : v.edges)
				{
					neigbore.edges.remove(v);
				}
				//System.out.println("pushed tmp no: " + v.temp_no);
				return ;
			}
		}
		assert false; //TODO: if more than 8 physical reg, print error
	}
	
	
	//step3:
	public void paint_vertex(Vertex v_2_Vcolor){
		int color_for_v = 0;
		int[] colors_in_use = new int[REGISTERS_AMOUNT];
		
		//get all colors that neighbors are using. 
		// note: each neighbore already has been colored.
		for(Vertex v : v_2_Vcolor.edges){
			//System.out.println("color[" + v.color+"] in use");
			//System.out.println("neighbor no: " + v.temp_no);
			
			colors_in_use[v.color] = 1;
		}

		// paint!
		for(int c=0; c<REGISTERS_AMOUNT; c++){
			if(colors_in_use[c] == 0){
				temps_colors.put(v_2_Vcolor.temp_no, c);
				v_2_Vcolor.color = c;
				break;
			}
		}
	}
	
	//for debugging:
	public void print_graph_lamelly(){
		System.out.println("print_graph_lamelly");
		System.out.println("Vertices:");
		for(Vertex v:V)
			v.print();
			
		System.out.println("printing graph finished");

	}
}
