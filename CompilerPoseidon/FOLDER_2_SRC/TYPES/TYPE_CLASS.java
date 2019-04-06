package TYPES;
import SYMBOL_TABLE.Variable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class TYPE_CLASS extends TYPE
{
	/*********************************************************************/
	/* If this class does not extend a father class this should beq null  */
	/*********************************************************************/
	public TYPE_CLASS father;
	public TYPE_FUNCTION ctor;
	/**************************************************/
	/* Gather up all data members in one place        */
	/* Note that data members coming from the AST are */
	/* packed together with the class methods         */
	/**************************************************/
//	public List<TYPE_CLASS_MEMBER> data_members;
    public List<Variable> classVariables;

	public List<String> fields;
	public LinkedHashMap<String, TYPE_FUNCTION> functions;
	
	/****************/
	/* CTROR(S) ... */
	/****************/
	public TYPE_CLASS(TYPE_CLASS father, String name)
	{
		this.name = name;
		this.father = father;
//		this.data_members = new ArrayList<>(); // just scraped the argument which was always null
		this.classVariables = new ArrayList<>();
		this.fields = new ArrayList<>();
		this.functions = new LinkedHashMap<>();
	}


	//IT IS A CLASS!
	public TYPE_CLASS asClass() { return this; }
	
	//IT IS NULLABLE!
	public boolean is_nullable() { return true; }
	
	
	public boolean is_derived_from(TYPE t){
		TYPE_CLASS curr= this;
		if(t == null) return false;
		TYPE_CLASS other = t.asClass();
		while(curr != null){
			if (curr.name.equals(other.name)) { return true;}
			curr = curr.father;
		}
		return false;
	}

	public boolean canBecome(TYPE t) {
		return (t == TYPE_NIL.getInstance()) || ((t.asClass() != null) && t.asClass().is_derived_from(this));
	}

	public TYPE findMember(String name) {
	    Variable t = getVarFor(name);
	    if (t != null) {
	        return t.type;
        }
        return getFunction(name);
	}

	public void printMembers() {
        System.out.format("variables:\n");
        for (Variable v: this.classVariables) {
            System.out.format("\t%s\t\n", v.type);
        }
        System.out.format("methods:\n");

        for (String it: functions.keySet()) {
			System.out.format("\t%s\n", it);
		}
	}

	public Variable getField(String field_name){
		return Variable.findByName(classVariables, field_name);
	}
	
	
	public int getFunctionIndex(String field_name){
	    // todo: tripple check that this yields same number for all subclasses
		int found = new ArrayList<>(functions.keySet()).indexOf(field_name);
		//error
		assert (found != -1);
		return found /*+ 1 ;// why +1?? */ ;
	}

	public TYPE_FUNCTION getFunction(String name) {
	    return functions.getOrDefault(name, null);
    }
    public int getAllocationWords() {
	    return getFieldsSize() + 1;
    }
	public int getFieldsSize(){
		return this.fields.size();
	}

	// we need this to makes sure that the methods that we output are
	public List<String> fullyQualifiedFunctionNames() {
	    // todo: this is bad, as some methods are implemented by a father function and we have no idea.
        // todo: add a link to the parent in every method, use it's name
	    ArrayList<String> names  = new ArrayList<>(functions.size());
	    for (TYPE_FUNCTION f : functions.values()) {
	        names.add(f.fullyQualifiedName());
        }
        return names;
    }

    public void addField(String name, Variable t) {
        fields.add(name);
        classVariables.add(t);
    }

    public void addFunc(String name, TYPE_FUNCTION t) {
        functions.put(name, t);
    }

    public Variable getVarFor(String name) {
	    return Variable.findByName(classVariables, name);
    }
}
