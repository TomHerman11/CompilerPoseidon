package AST;
import IR.*;
import MIPS.MIPSER;
import TEMP.*;
import TYPES.*;
import SYMBOL_TABLE.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public abstract class AST_CLASSDEC extends AST_Node
{	
	public String class_name;
	public String father_name;
	public TYPE_CLASS parsed_class_type;
	public List<AST_CFIELD> l;
	
	public AST_CLASSDEC(String class_name, String father_name, AST_CFIELD_LIST l)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.format("====================== classDec-> extends\n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.class_name	= class_name;		
		this.father_name = father_name;
		this.l = new ArrayList<>();
		for (; l != null; l = l.tail) {
		    this.l.add(l.head);
        }
	}
	
	public TYPE SemantMe() throws AST_SEMANTIC_ERROR {
	    TYPE_CLASS father_as_class = null;
		if(father_name != null) {
			//GET THE TYPE OF FATHER CLASS
			TYPE father_type = SYMBOL_TABLE.getInstance().find(this.father_name);
			if (father_type == null) {
				reportSemanticError("Class should beq extended, not extended");
                return null; // to help IDE inspectino
			}
			father_as_class = father_type.asClass();
			if(father_as_class == null) {
                reportSemanticError(String.format("father class: %s is not a class at symboltable\n", father_name));
            }
		}
        return SemantMeImplement(father_as_class);
	}
	
	private TYPE SemantMeImplement(TYPE_CLASS father_type_class) throws AST_SEMANTIC_ERROR {
		if(!SYMBOL_TABLE.getInstance().is_global_scope()) {
			reportSemanticError("Class declaration not in global scope");
		}
		
		//CHECK THAT CLASS NAME IS FIRST OF ITS NAME
		TYPE class_t = SYMBOL_TABLE.getInstance().find(class_name);
		if (class_t != null) {
			reportSemanticError("Class name is already defined");
		}
			
		//NOW SEMANT OVER DATA MEMBERS AND FUNCTIONS' NAMES
		String tmp_name;
		TYPE tmp_type;

		//creat a new TYPE_CLASS for this instance of class:
		TYPE_CLASS my_class_type = new TYPE_CLASS(father_type_class , class_name); //will update data_members later on

		//BEGIN SCOPE in SYMBOL TABLE, this also enter the class name to the table
		SYMBOL_TABLE.getInstance().beginScope(my_class_type);

		HashSet<String> members_set= new HashSet<String>();
		
		//insert to data_members THE data members and function names
		for(AST_CFIELD field : this.l) {
		    // the field.name is the same as the vardec/funcdec name, no harm in inserting
            // to the class prior to here, using the name for lookup only later
			tmp_type = field.SemantMeVarsAndFunctionSignature(members_set);
		}

		
		//go over functions' bodies
		for(AST_CFIELD field : this.l) {
			field.SemantMeFunctionBody();
		}
		
		//END SCOPE in SYMBOL TABLE
		SYMBOL_TABLE.getInstance().endScope(my_class_type);
		// create constructor
		this.parsed_class_type = my_class_type;
		parsed_class_type.ctor = new TYPE_FUNCTION(parsed_class_type, "ctor",
				Collections.singletonList(parsed_class_type), parsed_class_type);
		parsed_class_type.ctor.traceable = false;

		parsed_class_type.ctor.addArg(null); // only to have the correct length
		//ALL IS WELL
		return TYPE_VOID.getInstance();
	}

    @Override
    public TEMP IRme() {
        // a class needs to have it's vtable defined in the data section.
        IR.getInstance().addData(new IrVtableExpression(class_name, parsed_class_type));
        // a class needs to have a constructor emitted with the default values of args assigned.
        IRConstructor();
        IRMethods();
        return null; // nothing to do with the return value
	}

	private void IRMethods() {
		for (AST_CFIELD cf : this.l) {
			cf.IRMethod();
		}
	}

	public void IRConstructor() {
	    assert(class_name != null && parsed_class_type != null);
        // a constructor is a function that returns a newly allocated pointer.
        // it has no locals and no inputs, and doesn't call any other function.
		// no need for all the fancy function prologue things.a
		IR irrer = IR.getInstance();
        TEMP_FACTORY tf = TEMP_FACTORY.getInstance();
        // allocate the number of fields, add 1 for vtable.
		irrer.Add_IRcommand(new IRFunctionStart(parsed_class_type.ctor.fullyQualifiedName()));
		irrer.Add_IRcommand(new IRcommand_FunctionPrologue(parsed_class_type.ctor));
		TEMP obj = tf.getFreshTEMP();
		irrer.Add_IRcommand(new IRCommand_Arg.Load(obj, 0));
		// insert a call to the parent constructor
		if (parsed_class_type.father != null) {
			irrer.Add_IRCommands(AST_ABSTRACT_FUNC_IMPL.irActualCallTo(
					tf.getImmediateTEMP(parsed_class_type.father.ctor.fullyQualifiedName()),
					obj, Collections.singletonList(obj)
			));
		}
		// overwrite previous vtable
		TEMP vtable_offset = TEMP_FACTORY.getInstance().getFreshTEMP();
		irrer.Add_IRcommand(new IRcommand_LoadAddress(vtable_offset,
				IR_DATA_SECTION.getInstance().vtableNameFor(class_name)));
		irrer.Add_IRcommand(new IRcommand_Store(obj, vtable_offset));

		for (AST_CFIELD cf : this.l) {
            cf.IRConstructor(obj);
        }
        irrer.Add_IRcommand(new IRCommand_SetRetVal(obj));
		irrer.Add_IRcommand(new IRcommand_FunctionEpilogue(parsed_class_type.ctor));
		irrer.Add_IRcommand(new IRcommand_FunctionEnd());
    }
}
