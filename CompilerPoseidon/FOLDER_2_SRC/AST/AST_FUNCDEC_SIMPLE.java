package AST;
import IR.IR;
import TEMP.TEMP;
import TYPES.*;

import IR.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import SYMBOL_TABLE.*;

public class AST_FUNCDEC_SIMPLE extends AST_FUNCDEC
{
    class IdPair {
        String name, type;
        IdPair(String name, String type) { this.name = name; this.type = type;}
    }
	/************************/
	/* simple variable name */
	/************************/
	// inherits string name
	public String        returnTypeName;
	public TYPE returnType; // to beq filled
	public List<IdPair> args;
	public AST_ID_LIST argsAsBadType;
	public AST_STMT_LIST funcBody;
	public boolean       first_semant_me;
    private TYPE_FUNCTION function_type; // the function type now holds the number of locals and args
    private List<TYPE> param_list;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_FUNCDEC_SIMPLE(String returnTypeName, String funcName, AST_ID_LIST args, AST_STMT_LIST funcBody)
	{
		/*******************************/
		/* COPY INPUT DATA MEMBERS ... */
		/*******************************/
		super(funcName); //function name goes to SUPER. everything else, in here.
		this.returnTypeName = returnTypeName;
        this.args = new ArrayList<>();
		for (; args != null; args = args.tail) {this.args.add(new IdPair(args.name, args.type));}
		this.funcBody = funcBody;
		this.first_semant_me=true;
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();
		
		/******************************/
		/* IS IT THE MAIN FUNCTION?
		 * UPDATE THE SYMBOL TABLE THAT THERE IS A VOID MAIN() FUNCTION IN THE PROGRAM */
		/******************************/		
		if (funcName.equals("main") && returnTypeName.equals("void") && (this.args.size()==0)) {
			SYMBOL_TABLE.getInstance().containsValidMain = true;
		}
		
	}


	/*********************************************************/
	/* The printing message for a simple funcDec AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/********************************************/
		/* AST NODE TYPE = AST SIMPLE FUNCDEC */
		/********************************************/
		System.out.print("AST NODE SIMPLE FUNCDEC\n");

		/***********************************/
		/* RECURSIVELY PRINT  returnType + funcBody ... */
		/***********************************/	

		if (argsAsBadType != null) argsAsBadType.PrintMe();
		if (funcBody != null) funcBody.PrintMe();


		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("SIMPLE FUNCDEC\n()(%s) (%s)",returnTypeName, name));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (argsAsBadType != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,argsAsBadType.SerialNumber);

		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,funcBody.SerialNumber);
	}

/***
 SemantMe:
 1. this semantme is from method (in class) declaration.
 2. need to provide SemantMeSignature and SemantMeBody;.
 3. no "real" Semantme is needed here.
***/
	
	/***********************************/
	/****** SemantMeSignature **********/
	/***********************************/
	public TYPE SemantMeSignature(HashSet<String> members_set) throws AST_SEMANTIC_ERROR {
		TYPE func_t= null;
		SYMBOL_TABLE symbol_table = SYMBOL_TABLE.getInstance();
		//Check if the name already in the SYMBOL TABLE. then check if it's a type's name (int,string,class,array)
		func_t = symbol_table.findTypeAndVoid(name);
		if(func_t != null) {
			//ERROR: function's name can't beq a type's name
			reportSemanticError("Function name can't beq a type's name");
			//System.exit(0); //FIX ERROR
		}
		
		//check that there is no other class field/ function with the same name in the class
		if(members_set.contains(name)) {
			reportSemanticError(String.format("function %s already defined within scope", name));
		}
		members_set.add(name);
		//check return type
		returnType= checkReturnType();
		//check args type
		this.param_list = checkArgsTypesAndNames();
		
		//check if there exists such function in FATHER CLASS. OVERRIDE?
		func_t = symbol_table.findInScope(name);
		if(func_t!= null){
		    assertMatchesOverridingFuncMatches(func_t.asFunction(), param_list);
		}
		
		//make a new FUNC_TYPE for this function
		this.function_type = new TYPE_FUNCTION(returnType, name, param_list, symbol_table.curr_type_class);

		// we're not starting the function scope here, but we ARE entering it to the scope
		symbol_table.enterFunction(name, function_type);
		return function_type;
	}
	
	private void assertMatchesOverridingFuncMatches(TYPE_FUNCTION father_func_t, List<TYPE> param_list)
            throws AST_SEMANTIC_ERROR {
        if (father_func_t == null) {
            //ERROR - same name, not override!!!
            reportSemanticError(String.format("name %s already defined within scope", name));
            // System.exit(0);
            return;
        }

        //if there is a function with same name - make sure it's the same signature! (else ERROR) - COMPARE:
        //same return type?
        if (!father_func_t.returnType.name.equals(returnType.name)) {
            reportSemanticError(String.format("name %s already defined within scope", name));
            // System.exit(0);
            return;
        }

        //same args type?
        if (!compareArgsListsTypes(father_func_t.params, param_list)) {
            reportSemanticError(String.format("name %s already defined within scope", name));
            // System.exit(0);
        }
    }
	/****************************************/
	/****** AUX - SemantMeSignature**********/
	/****************************************/
	/*** LOOK for function return type in SYMBOL TABLE ***/
    private TYPE checkReturnType() throws AST_SEMANTIC_ERROR {
		
		TYPE returnType = SYMBOL_TABLE.getInstance().findTypeAndVoid(returnTypeName);
		if(returnType == null) { 
			reportSemanticError(String.format("Non existing return type %s\n", returnTypeName));
			// System.exit(0)
			return null;
		}
		
		return returnType;
	}
	
	/*** CHECK function's arguments types and declaration ***/
	public List<TYPE> checkArgsTypesAndNames () throws AST_SEMANTIC_ERROR  {
	    // change: I removed the opening of a scope here because it's unnecessary.
        // we're no really looking at a new scope, we're just checking that it's correct.
        // we'll input the args into the scope later, when we semant the body.
		ArrayList<TYPE> my_type_list = new ArrayList<>();
		SYMBOL_TABLE symbol_table = SYMBOL_TABLE.getInstance();
		HashSet<String> entered_names = new HashSet<>();
		for (IdPair it : args) {
            TYPE t = symbol_table.findType(it.type);
            if (t == null) {
                reportSemanticError(String.format("non existing type %s\n", it.type));
                // System.exit(0);
                return null;
            }
            TYPE name_t = symbol_table.findTypeAndVoid(it.name);
            if (name_t != null) {
                reportSemanticError(String.format("argument's name can't beq an existing type %s\n", it.name));
                // System.exit(0);
                return null;
            }
			/* check that it.name doesn't already exist in scope.
			 * since the scope is only the args, we just need to check for names inserted as args.
			 */
//			name_t = symbol_table.findInScope(it.name);
//			if (name_t != null) {
//				reportSemanticError(String.format("arguments' name: %s already exists in scope\n", it.name));
//				// System.exit(0);
//				return null;
//			}
			if (!entered_names.add(it.name)) {
				reportSemanticError("arguments' name: %s already defined as arg\n");
            }
            my_type_list.add(t);
        }
		return my_type_list;
	}

	public boolean compareArgsListsTypes(List<TYPE> l1, List<TYPE> l2) {
		if (l1 == null && l2 == null) { return true; }
		if (l1 == null || l2 == null) { return false; }
		if (l1.size() != l2.size()) return false;

		Iterator<TYPE> i2 = l2.iterator();
		for (TYPE t1 : l1) {
			if (!t1.name.equals(i2.next().name)) return false;
		}
		return true;
	}

	/***********************************/
	/****** SemantMeBody **********/
	/***********************************/
	//SEE IF THE CHECKS THAT THE FUNCTION IN THE SYMBOL TABLE MATCHES THE FUNCTION'S SIGNATURE ARE NEEDED. IF NOT- REMOVE THEM
	//RON: there is no need to work with SymbolTable.
	public TYPE SemantMeBody() throws AST_SEMANTIC_ERROR {
	    
		TYPE 		return_t  = null;
		TYPE_LIST 	type_list 	= null;

		/****************************/
		/* [1] Begin Function Scope */
		/****************************/
		SYMBOL_TABLE.getInstance().beginScope(this.function_type);
        Iterator<IdPair> it = this.args.iterator();
		SYMBOL_TABLE.getInstance().enterArgument("this", this.function_type.getImplementor());
		for (TYPE t: this.param_list)
		{
		    //todo: make sure that the arg names are checked throughly in the SemnatSignature part.
            //todo: add the 'this' first argument since this is the flow for a class function.
            SYMBOL_TABLE.getInstance().enterArgument(it.next().name, t);
        }
		
		//Assume that (#returnStament = 0)  is legal
		funcBody.SemantMe();
		SYMBOL_TABLE.getInstance().endScope(this.function_type);
		//We don't need the return type, so returning void is okay
		return TYPE_VOID.getInstance();
	}
	
	
	/***********************************/
	/****** SemantMe **********/
	/***********************************/
	public TYPE SemantMe() throws AST_SEMANTIC_ERROR
    {
		TYPE func_t= null;
        int cnt_args = 0;
		System.out.println(String.format("%s semantme", this.getClass().getSimpleName()));
		// Check the name doesn't already in the SYMBOL TABLE
		func_t = SYMBOL_TABLE.getInstance().find(name);
		if(func_t != null) {
			reportSemanticError("function's name already used in the scope (global scope)");
            // System.exit(0); //FIX ERROR
		}
        // check the return type exists.
		TYPE return_t = checkReturnType();
		// resolve the parameter types are correct
		List<TYPE> resolved_params = checkArgsTypesAndNames();

		// al the checks passed - create the function type
        this.function_type = new TYPE_FUNCTION(return_t, name,
                resolved_params.size() > 0 ? resolved_params : null);

        // put the function in the symbol_table, start a scope
        SYMBOL_TABLE.getInstance().beginScope(this.function_type);


        // push the arguments to the symbol_table, add them as arguments
        Iterator<IdPair> it = args.iterator();
        for (TYPE t : resolved_params) {
            SYMBOL_TABLE.getInstance().enterArgument(it.next().name, t);
        }

        // this is a global function, all the declaration checks passed, we can check the body
		funcBody.SemantMe();

        SYMBOL_TABLE.getInstance().endScope(this.function_type);

		//We don't need the return type, so returning void is okay
		return TYPE_VOID.getInstance();
	
	}

    @Override
    public TEMP IRme() {
		// store the function name in the data section so we can do PrintTrace
		IR_DATA_SECTION.getInstance().addFunctionNameLabel(function_type);

	    // a function is a label with instructions, and a ret in the end. it has a prologue and an epilogue.
        // the prologue allocates stack space, the epilogue frees it.
		IR.getInstance().Add_IRcommand(
				new IRFunctionStart(function_type)
		);
		IR.getInstance().Add_IRcommand(
                new IRcommand_FunctionPrologue(function_type)
        );

        funcBody.IRme();

        IR.getInstance().Add_IRcommand(new IRcommand_Label(IR_DATA_SECTION.getInstance().
				getFunctionExitLabel(function_type)));
        IR.getInstance().Add_IRcommand(new IRcommand_FunctionEpilogue(function_type));
        IR.getInstance().Add_IRcommand(new IRcommand_FunctionEnd());
        return null;
    }
}
