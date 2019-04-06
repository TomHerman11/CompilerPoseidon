package TYPES;

import SYMBOL_TABLE.Variable;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class TYPE_FUNCTION extends TYPE
{
	/***********************************/
	/* The return type of the function */
	/***********************************/
	public TYPE returnType;

	/*************************/
	/* types of input params */
	/*************************/
	public List<TYPE> params;
	public List<Variable> args;
	public List<Variable> locals;
	final TYPE_CLASS implementor;
	public boolean traceable = true;
	public boolean crt = false;
	/****************/
	/* CTROR(S) ... */
	/****************/
	public TYPE_FUNCTION(TYPE returnType,String name,List<TYPE> params, TYPE_CLASS implementor)
	{
		this.name = name;
		if (implementor == null && name.equals("main")) {
			this.name = "_main";
			this.crt = true;
		}
		this.returnType = returnType;
		this.params = params; // may beq null if there are no arguments
		this.args = null;
		this.implementor = implementor;
	}
	public TYPE_FUNCTION(TYPE returnType,String name,List<TYPE> params)
	{
		this(returnType, name, params, null);
	}

	private boolean listSizeMismatch(List<TYPE> given) {
			int givenSize = given != null ? given.size() : 0;
			int mySize = params != null ? params.size() : 0;
			return givenSize != mySize;
	}
	
	public boolean matchArguments(List<TYPE> given) {
		if (listSizeMismatch(given)) return false;
		if (params == null || given == null) {
			return true; // size is 0, checked before.
		}
		Iterator<TYPE> others = given.iterator();
		for (TYPE t : params) {
			if (!t.canBecome(others.next())) return false;
		}
		return true;
	}
	
	//IT IS A FUNCTION
	public TYPE_FUNCTION asFunction(){ return this;}
	public boolean isMethod() {return implementor != null; }
	private Variable nameFromList(List<Variable> which, String name) {
		for (Variable i : which) {
			if (i.name.equals(name)) {
				return i;
			}
		}
		return null;
	}

	public Variable getVar(String name) {
		Variable out = nameFromList(args, name);
		out = out == null ? nameFromList(locals, name) : out;
		return out;
	}

	public int argCount() { return args == null? 0 : args.size(); }
	public int localCount() {return locals == null ? 0 : locals.size(); }

	public void addArg(Variable metadata) {
		if (args == null) {
			args = new ArrayList<>();
		}
		args.add(metadata);
	}
	public void addLocal(Variable metadata) {
		locals = locals != null ? locals : new ArrayList<>();
		locals.add(metadata);
	}

	public TYPE_CLASS getImplementor() {return implementor;}

	public String fullyQualifiedName() {
		if (crt) return name;
		String suffix = name;
		if (isMethod()) {
			suffix = getImplementor().getName() + "_" + suffix;
		}
		return "_func_"  + suffix;

	}
}
