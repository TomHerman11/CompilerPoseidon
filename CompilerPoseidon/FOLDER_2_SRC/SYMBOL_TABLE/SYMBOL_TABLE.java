/***********/
/* PACKAGE */
/***********/
package SYMBOL_TABLE;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Map;

/*******************/
/* PROJECT IMPORTS */
/*******************/
import IR.CRTSyscall;
import IR.IR_DATA_SECTION;
import TYPES.*;

/****************/
/* SYMBOL TABLE */
/****************/
public class SYMBOL_TABLE
{
	private int hashArraySize = 13;
	
	//our defined class members
	private int			 scope 		= 0; // 0 is the global scope
	//private TYPE_CLASS 	 in_class 	= null;

	/**********************************************/
	/* The actual symbol table data structure ... */
	/**********************************************/
	private SYMBOL_TABLE_ENTRY[] table = new SYMBOL_TABLE_ENTRY[hashArraySize];
	private SYMBOL_TABLE_ENTRY top;
	private int top_index = 0;
	
	/**** EX4 ****/
	//determine function size
	public TYPE_CLASS curr_type_class = null;
	public TYPE_FUNCTION curr_type_function = null;
	
	//verify there is a ***void main()*** function:
	public boolean containsValidMain = false;
	

	/**************************************************************/
	/* A very primitive hash function for exposition purposes ... */
	/**************************************************************/
	private int hash(String s)
	{
		if (s.charAt(0) == 'l') {return 1;}
		if (s.charAt(0) == 'm') {return 1;}
		if (s.charAt(0) == 'r') {return 3;}
		if (s.charAt(0) == 'i') {return 6;}
		if (s.charAt(0) == 'd') {return 6;}
		if (s.charAt(0) == 'k') {return 6;}
		if (s.charAt(0) == 'f') {return 6;}
		if (s.charAt(0) == 'S') {return 6;}
		return 12;
	}

	/****************************************************************************/
	/* Enter a variable, function, class type or array type to the symbol table */
	/****************************************************************************/
	public void enter(String name,TYPE t)
	{
		
		/*************************************************/
		/* [1] Compute the hash value for this new entry */
		/*************************************************/
		int hashValue = hash(name);

		/******************************************************************************/
		/* [2] Extract what will eventually beq the next entry in the hashed position  */
		/*     NOTE: this entry can very well beq null, but the behaviour is identical */
		/******************************************************************************/
		SYMBOL_TABLE_ENTRY next = table[hashValue];
	
		/**************************************************************************/
		/* [3] Prepare a new symbol table entry with name, type, next and prevtop */
		/**************************************************************************/
		SYMBOL_TABLE_ENTRY e = new SYMBOL_TABLE_ENTRY(name,t,hashValue,next,top,top_index++, null);

		/**********************************************/
		/* [4] Update the top of the symbol table ... */
		/**********************************************/
		top = e;
		
		/****************************************/
		/* [5] Enter the new entry to the table */
		/****************************************/
		table[hashValue] = e;
		
		/**************************/
		/* [6] Print Symbol Table */
		/**************************/
		PrintMe();
	}

	// this function handles only local, class, and global variables. arguments are added separately
	private Variable initNewVar(String name, TYPE t) {
		Variable out;
		if (curr_type_function != null) {
			out = new LocalVariable(name, t, curr_type_function.localCount());
			curr_type_function.addLocal(out);
		}
		else if (curr_type_class != null) {
			out = new ClassVariable(name, t, curr_type_class.getFieldsSize());
			//curr_type_class.classVariables.add(out);
			curr_type_class.addField(name, out);
		}
		else {
		    out = new GlobalVariable(name, t);
        }
		return out;
	}

	public Variable enterVariable(String name, TYPE t) {
		Variable metadata = initNewVar(name, t);
		enter(name, t, metadata);
		//if (metadata instanceof ClassVariable) curr_type_class.addField(name, metadata);
		//else if (!(metadata instanceof GlobalVariable)) curr_type_function.addLocal(metadata);
		return metadata;
	}

	public Variable enterArgument(String name, TYPE t) {
		assert curr_type_function != null;
		ArgumentVariable metadata = new ArgumentVariable(name, t, curr_type_function.argCount());
		enter(name, t, metadata);
		curr_type_function.addArg(metadata);
		return metadata;
	}

	public void enterFunction(String name, TYPE_FUNCTION t) {
		enter(name, t);
		if (curr_type_class != null) {
			curr_type_class.addFunc(name,t);
		}
		enter(name, t);
	}

	//OVERLOADING - this wonderful function inserts vars to the SYMBOLE_TABLE, in order to know if the var is global/local/data_members etc...
	public void enter(String name,TYPE t, Variable metadata)
	{
		/*************************************************/
		/* [1] Compute the hash value for this new entry */
		/*************************************************/
		int hashValue = hash(name);

		/******************************************************************************/
		/* [2] Extract what will eventually beq the next entry in the hashed position  */
		/*     NOTE: this entry can very well beq null, but the behaviour is identical */
		/******************************************************************************/
		SYMBOL_TABLE_ENTRY next = table[hashValue];
	
		/**************************************************************************/
		/* [3] Prepare a new symbol table entry with name, type, next and prevtop */
		/**************************************************************************/
		SYMBOL_TABLE_ENTRY e = new SYMBOL_TABLE_ENTRY(name,t,hashValue,next,top,top_index++, metadata);

		/**********************************************/
		/* [4] Update the top of the symbol table ... */
		/**********************************************/
		top = e;
		
		/****************************************/
		/* [5] Enter the new entry to the table */
		/****************************************/
		table[hashValue] = e;
		
		/**************************/
		/* [6] Print Symbol Table */
		/**************************/
		PrintMe();
	}

	/***********************************************/
	/* Find the inner-most scope element with name */
	/***********************************************/
	public TYPE find(String name)
	{
		SYMBOL_TABLE_ENTRY e;

		for (e = table[hash(name)]; e != null; e = e.next)
		{
			if (name.equals(e.name))
			{
				return e.type;
			}
		}
		
		return null;
	}
	
	
	/***********************************************/
	/* Find the element inside the latest scope */
	/***********************************************/
	public TYPE findInScope(String name)
	{
		SYMBOL_TABLE_ENTRY temp_top = top;
		while (temp_top!= null && !(temp_top.type instanceof TYPE_FOR_SCOPE_BOUNDARIES))
		{
			if(name.equals(temp_top.name)){
				return temp_top.type;
			}
			temp_top = temp_top.prevtop;
		}
		return null;	
	}
	
	/***********************************************/
	/* search if exists class with name */
	/***********************************************/
	public TYPE findClassInSymbolTable(String name){
		SYMBOL_TABLE_ENTRY e;	
		for (e = table[hash(name)]; e != null; e = e.next)
			if (name.equals(e.name) && (e.type instanceof TYPE_CLASS))
				return e.type;		
		return null;
	}
	
	
	/**********************************************************/
	/* search if name is an existing type in the SYMBOL TABLE */
	/**********************************************************/
	//if name is not a type- return null. if name is a type - return the type.
	//if name=="void"- return null; it's not a legal type in the program (other than function's return type)
	public TYPE findType(String name){
		if(name.equals("void")) {
			return null;
		}
		TYPE t = find(name);
		if (t == null) {
			return null;
		}
		if (t.asFunction()==null &&  name.equals(t.name)) {
			return t;
		}
		//if name is not a type- return null
		return null;
	}

	//if name is not a type- return null. if name is a type - return the type.
	//if name== "void"- return TYPE_VOID
	public TYPE findTypeAndVoid(String name){
		TYPE t = find(name);
		if (t == null) {
			return null;
		}
		if (t.asFunction()==null &&  name.equals(t.name)) {
			return t;
		}
		//if name is not a type- return null
		return null;
	}
	
	/***********************************************/
	/* search if exists array with name */
	/***********************************************/
	public TYPE findArrayInSymbolTable(String name){
		SYMBOL_TABLE_ENTRY e;
		for (e = table[hash(name)]; e != null; e = e.next)
			if (name.equals(e.name) && (e.type instanceof TYPE_ARRAY))
				return e.type;
		return null;
	}
	
	
	/***********************************************/
	/* findmetadata, for IRMe */
	/***********************************************/	
	public Variable findMetadata(String name)
	{
		SYMBOL_TABLE_ENTRY e;

		for (e = table[hash(name)]; e != null; e = e.next)
		{
			if (name.equals(e.name))
			{
				return e.metadata;
			}
		}
		return null;
	}


	/***************************************************************************/
	/* begine scope = Enter the <SCOPE-BOUNDARY> element to the data structure */
	/***************************************************************************/
	private void beginScopeCommon(String scopeName) {
		scope ++;
		/************************************************************************/
		/* Though <SCOPE-BOUNDARY> entries are present inside the symbol table, */
		/* they are not really types. In order to beq ablt to debug print them,  */
		/* a special TYPE_FOR_SCOPE_BOUNDARIES was developed for them. This     */
		/* class only contain their type name which is the bottom sign: _|_     */
		/************************************************************************/
		TYPE scope_type = curr_type_function != null ? curr_type_function.returnType : null;
		enter(
				"SCOPE-BOUNDARY",
				new TYPE_FOR_SCOPE_BOUNDARIES(scopeName, scope_type));

		/*********************************************/
		/* Print the symbol table after every change */
		/*********************************************/
		PrintMe();
	}
	public void beginScope() { 	beginScopeCommon("ScopeBoundary");	}

	public void beginScope(TYPE_CLASS tc) {
		// todo: consider moving the 3 statements to a separate enterClass function
		curr_type_class = tc;
		enter(tc.name, tc);
		beginScopeCommon(tc.name);
		updateWithFatherClass(tc.father);
	}

	public void updateWithFatherClass(TYPE_CLASS father_type_class) {
		if (father_type_class == null) return;
		// todo: code broken, fix
		//insert. the father class contains all the variables of the child.
		for(Variable it : father_type_class.classVariables) {
			enterVariable(it.name, it.type);
		}
		for (Map.Entry<String, TYPE_FUNCTION> func : father_type_class.functions.entrySet()) {
			enterFunction(func.getKey(), func.getValue());
		}
	}
	// any function scope that is begining should expect the return value to beq the same as the function, so we
	// save that.
	public void beginScope(TYPE_FUNCTION tf) {
		curr_type_function = tf;
		// function may have been added in the process of parsing a class.
		if (find(tf.name) == null) { enterFunction(tf.name, tf); }
		beginScopeCommon(tf.name);
	}

	//return the expected return type of the scope
	public TYPE getScopeType()
	{
		SYMBOL_TABLE_ENTRY temp_top = top;
	
		while (!(temp_top.type instanceof TYPE_FOR_SCOPE_BOUNDARIES))
		{
			temp_top = temp_top.prevtop;
		}
		TYPE_FOR_SCOPE_BOUNDARIES tmp_t= (TYPE_FOR_SCOPE_BOUNDARIES) temp_top.type;
		return tmp_t.return_type;
	}


	public void endScope() { endScopeCommon(); }
	public void endScope(TYPE_CLASS tc) {
		assert (this.curr_type_class == tc && this.curr_type_function == null);
		endScopeCommon();
		curr_type_class = null;
	}

	public void endScope(TYPE_FUNCTION tf) {
		assert (this.curr_type_function == tf);
		endScopeCommon();
		curr_type_function = null;
	}

	/********************************************************************************/
	/* end scope = Keep popping elements out of the data structure,                 */
	/* from most recent element entered, until a <NEW-SCOPE> element is encountered */
	/********************************************************************************/
	private void endScopeCommon()
	{
		scope --;
		/**************************************************************************/
		/* Pop elements from the symbol table stack until a SCOPE-BOUNDARY is hit */		
		/**************************************************************************/
		while (!(top.type instanceof TYPE_FOR_SCOPE_BOUNDARIES))
		{
			table[top.index] = top.next;
			top_index = top_index-1;
			top = top.prevtop;
		}
		/**************************************/
		/* Pop the SCOPE-BOUNDARY sign itself */		
		/**************************************/
		table[top.index] = top.next;
		top_index = top_index-1;
		top = top.prevtop;

		/*********************************************/
		/* Print the symbol table after every change */		
		/*********************************************/
		PrintMe();
	}
	
	public static int n=0;
	
	public void PrintMe()
	{
		int i=0;
		int j=0;
		String dirname="./FOLDER_5_OUTPUT/";
		String filename=String.format("SYMBOL_TABLE_%d_IN_GRAPHVIZ_DOT_FORMAT.txt",n++);

		try
		{
			/*******************************************/
			/* [1] Open Graphviz text file for writing */
			/*******************************************/
			PrintWriter fileWriter = new PrintWriter(dirname+filename);

			/*********************************/
			/* [2] Write Graphviz dot prolog */
			/*********************************/
			fileWriter.print("digraph structs {\n");
			fileWriter.print("rankdir = LR\n");
			fileWriter.print("node [shape=record];\n");

			/*******************************/
			/* [3] Write Hash Table Itself */
			/*******************************/
			fileWriter.print("hashTable [label=\"");
			for (i=0;i<hashArraySize-1;i++) { fileWriter.format("<f%d>\n%d\n|",i,i); }
			fileWriter.format("<f%d>\n%d\n\"];\n",hashArraySize-1,hashArraySize-1);
		
			/****************************************************************************/
			/* [4] Loop over hash table array and print all linked lists per array cell */
			/****************************************************************************/
			for (i=0;i<hashArraySize;i++)
			{
				if (table[i] != null)
				{
					/*****************************************************/
					/* [4a] Print hash table array[i] -> entry(i,0) edge */
					/*****************************************************/
					fileWriter.format("hashTable:f%d -> node_%d_0:f0;\n",i,i);
				}
				j=0;
				for (SYMBOL_TABLE_ENTRY it=table[i];it!=null;it=it.next)
				{
					/*******************************/
					/* [4b] Print entry(i,it) node */
					/*******************************/
					fileWriter.format("node_%d_%d ",i,j);
					fileWriter.format("[label=\"<f0>%s|<f1>%s|<f2>prevtop=%d|<f3>next\"];\n",
						it.name,
						it.type.name,
						it.prevtop_index);

					if (it.next != null)
					{
						/***************************************************/
						/* [4c] Print entry(i,it) -> entry(i,it.next) edge */
						/***************************************************/
						fileWriter.format(
							"node_%d_%d -> node_%d_%d [style=invis,weight=10];\n",
							i,j,i,j+1);
						fileWriter.format(
							"node_%d_%d:f3 -> node_%d_%d:f0;\n",
							i,j,i,j+1);
					}
					j++;
				}
			}
			fileWriter.print("}\n");
			fileWriter.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}		
	}
	
	/**************************************/
	/* USUAL SINGLETON IMPLEMENTATION ... */
	/**************************************/
	private static SYMBOL_TABLE instance = null;

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected SYMBOL_TABLE() {}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	public static SYMBOL_TABLE getInstance()
	{
		if (instance == null)
		{
			/*******************************/
			/* [0] The instance itself ... */
			/*******************************/
			instance = new SYMBOL_TABLE();

			/*****************************************/
			/* [1] Enter primitive types int, string */
			/*****************************************/
			instance.enter("int",   TYPE_INT.getInstance());
			instance.enter("string",TYPE_STRING.getInstance());
			instance.enter("void",TYPE_VOID.getInstance());

			/*************************************/
			/* [2] How should we handle void ??? */
			/*************************************/

			/***************************************/
			/* [3] Enter library function PrintInt */
			/***************************************/
			TYPE_FUNCTION crtFunc =
					new TYPE_FUNCTION(
							TYPE_VOID.getInstance(),
							CRTSyscall.PrintInt.name(),
							Collections.singletonList(TYPE_INT.getInstance()));
			crtFunc.crt = true;
			instance.enter(
                    CRTSyscall.PrintInt.name(),crtFunc);
			
			/***************************************/
			/* [4] Enter library function PrintString */
			/***************************************/
			crtFunc = new TYPE_FUNCTION(
					TYPE_VOID.getInstance(),
					CRTSyscall.PrintString.name(),
					Collections.singletonList(TYPE_STRING.getInstance()));
			crtFunc.crt = true;
			instance.enter(
                    CRTSyscall.PrintString.name(),
					crtFunc);
		
			/***************************************/
			/* [5] Enter library function PrintTrace */
			/***************************************/
			crtFunc = new TYPE_FUNCTION(
					TYPE_VOID.getInstance(),
					IR_DATA_SECTION.getInstance().getPrintTraceGlobalName(),
					null);
			crtFunc.crt = true;

			instance.enter(
                    IR_DATA_SECTION.getInstance().getPrintTraceGlobalName(),
				crtFunc); //no need for type_list
		}
		return instance;
	}

	/////////////////////////////////////////
	public boolean is_global_scope ()
	{
		return !is_in_class() && curr_type_function == null;
	}

	public int get_scope_number() {
		return scope;
	}


	public boolean is_in_class (){
		return curr_type_class != null;
	}

	public TYPE_CLASS getCurTypeClass() { return curr_type_class; }
	public TYPE_FUNCTION getCurTypeFunction() { return curr_type_function; }
}
