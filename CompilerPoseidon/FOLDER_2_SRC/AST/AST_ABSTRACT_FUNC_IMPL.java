package AST;
import TYPES.*;

import SYMBOL_TABLE.*;
import IR.*;
import TEMP.*;

import java.util.ArrayList;
import java.util.List;

// this is a base of a function call statement
public class AST_ABSTRACT_FUNC_IMPL extends AST_Node
{
	public String name;
	public AST_VAR var;
	public List<AST_EXP> exp_list;
	public int funcIndex; //for IRME!!!!!!!!!!!!!!!!!
    public TYPE_FUNCTION called_function;
	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_ABSTRACT_FUNC_IMPL(AST_VAR var, String name, List<AST_EXP> exp_list)
	{
		this.var = var; // may beq null
		this.name = name; // may NOT beq null
		this.exp_list = exp_list; // may beq null, since we inherit from AST_STMT_VAR_PARAM
		this.funcIndex = -1; 

	}
	public void setLineNumber(int l) {
		super.setLineNumber(l);
		System.out.format("function got line number %d\n",l);
	}
	private void validateFunctionCall(TYPE_FUNCTION func) throws AST_SEMANTIC_ERROR {
		if (func == null) {
			//reportSemanticError(String.format("%s%s is NOT a function but is reffered to as one",var == null ? "" : var.name + ".",name));
            reportSemanticError(String.format("%s%s is NOT a function but is reffered to as one",var == null ? "" : "somevar.",name));
            return; // not reached
		}
		List<TYPE>  arg_list = null;	
		if (exp_list != null) {
			
			arg_list = new ArrayList<>(exp_list.size());	
			for(AST_EXP e : exp_list) {
					arg_list.add(e.SemantMe());
			}	
		}

		if(!func.matchArguments(arg_list)){
			reportSemanticError(String.format("%s argument mismatch", func.name));
		}
		// goodie goodie

	}

	public TYPE SemantMe() throws AST_SEMANTIC_ERROR {
		// this handles all cases: 
		TYPE_CLASS var_class;
		TYPE member_type;
        SYMBOL_TABLE sym_table = SYMBOL_TABLE.getInstance();
		// we're in a class - SYMBOL_TABLE.current_class is set
		// we're not in a class - it's not set

		System.out.format("Looking at func %s", this.name);
		// 1. there is a var:
		if (var != null) {
			// if the var is before a function it is clearly a class.
			var_class = var.SemantMe().asClass();
			if (var_class == null) {
				reportSemanticError("function call with a non-class var");
				return null; // not reached
			}
			System.out.format ("Got class var tyep: %s, looking for: %s\n", var_class.name, this.name);
			var_class.printMembers();
			member_type = var_class.findMember(name);
			
			/*** EX4 ***/
			funcIndex = var_class.getFunctionIndex(name);
			/*** EX4 ***/
		}
		else {
			member_type = sym_table.find(name);
			
			/*** EX4 ***/
			TYPE_CLASS t_class = sym_table.getCurTypeClass();
			if(t_class != null){
				funcIndex = t_class.getFunctionIndex(name);
			}
			/*** EX4 ***/
		}
		
		if (member_type == null) {
			reportSemanticError(String.format("Could not find name %s %s", name, var == null ? "" : String.format("in var type")));
			return null; // not reached
		}

		validateFunctionCall(member_type.asFunction());
		called_function = member_type.asFunction();
		return member_type.asFunction().returnType;
	}

	private int expComparator(AST_EXP e1, AST_EXP e2) {
		return e1.nodeWeight - e2.nodeWeight;
	}
	private List<TEMP> irArguments() {
        // todo: sort the list
        List<TEMP> out = new ArrayList<>(exp_list == null ? 0 : exp_list.size());
        if (exp_list != null) {
            for (AST_EXP e : exp_list) {
                out.add(e.IRme());
            }
        }
        return out;
    }

	// a syscall has 1 arg and may output a value
	private static List<IRcommand> irSyscall(CRTSyscall syscall, TEMP arg) {
		ArrayList<IRcommand> callCmds = new ArrayList<>(3);
		callCmds.add(new IRCommand_Arg.Pass(arg, 0, 1));
		callCmds.add(new IRCommand_Call.Syscall(syscall));

		return callCmds;
	}

	private static List<IRcommand> irUserCall(TEMP addrContainer, List<TEMP> args){
		List<IRcommand> callCmds = new ArrayList<>(args.size() * 2 + 2);
		callCmds.add(new IRcommand_push(REGISTER.ra));
		callCmds.add(new IRCommand_Arg.Allocate(args.size()));
		for (int i = 0; i < args.size(); ++i) {
			callCmds.add(new IRCommand_Arg.Pass(args.get(i), i, args.size()));
		}
		callCmds.add(new IRCommand_Call(addrContainer));
		callCmds.add(new IRCommand_Arg.ClearAll(args.size()));
		callCmds.add(new IRcommand_pop(REGISTER.ra));
		return callCmds;
	}

	public static List<IRcommand> irActualCallTo(TEMP addrContainer, TEMP out, List<TEMP> args) {
        CRTSyscall syscall = CRTSyscall.syscallFor(addrContainer.toString());

		List<IRcommand> callCmds = syscall != CRTSyscall.None ?
                            irSyscall(syscall, args.get(0)) :
                            irUserCall(addrContainer, args);
		callCmds.add(0, new IRcommand_CallArgumentBoundry.Save());
		callCmds.add(0, new IRcommand_CallRegisterBoundry(true));
        callCmds.add(new IRcommand_CallRegisterBoundry(false));
        callCmds.add(new IRcommand_CallArgumentBoundry.Restore());
        if (out != null) {
            callCmds.add(new IRCommand_LoadCallResult(out));
        }
        return callCmds;
	}

	// the call destination is either the fully qualified function name, or a pointer from a vtable.
	// if we know the function index from the SemantMe stage, then this is a method call and we need to extract from
	// vtable.
    private TEMP getCallDest(TEMP thisarg) {
		if (funcIndex == -1) {
			return TEMP_FACTORY.getInstance().getImmediateTEMP(called_function.fullyQualifiedName());
		}

		assert thisarg != null;
		// allocate new temp for the function pointer
		TEMP dest = TEMP_FACTORY.getInstance().getFreshTEMP();
		// read the vtable pointer
		IR.getInstance().Add_IRcommand(new IRcommand_Load(dest, thisarg, 0));
		// read the function pointer (function index is given from semant me
		IR.getInstance().Add_IRcommand(new IRcommand_Load(dest, dest, funcIndex));
		return dest;
	}

	// if this is a regular function, there is no this.
	// otherwise - is a class method, it's either called with a var or from another class method.
	// either way we can get the this arg.
	private TEMP getThisForCall() {
		if (funcIndex == -1) return null; // no this for normal function
		TEMP thisArg;
		if (this.var != null) {
			thisArg = this.var.IRmeRead();
			// assert that this is a valid var
			IR.getInstance().Add_IRcommand(new IRcommand_Cond_Jump.Eq(thisArg, REGISTER.zero,
                    TEMP_FACTORY.getInstance().getImmediateTEMP(CRTError.NilDeref.exitLabelName())));
		}
		else {
			thisArg = TEMP_FACTORY.getInstance().getFreshTEMP();
			IR.getInstance().Add_IRcommand(new IRcommand_LoadThisArg(thisArg));
		}
		return thisArg;
	}

    public TEMP IRme() {
		List<TEMP> args = irArguments();
		TEMP thisArg = getThisForCall();
		TEMP destAddr = getCallDest(thisArg);
		if (null != thisArg) {
			args.add(0, thisArg);
		}
		TEMP out = (called_function.returnType != null && called_function.returnType != TYPE_VOID.getInstance())
                ? TEMP_FACTORY.getInstance().getFreshTEMP() : null;
		IR.getInstance().Add_IRCommands(irActualCallTo(destAddr, out, args));
		return out;
    }
	
}
