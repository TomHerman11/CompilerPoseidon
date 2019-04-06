package AST;
import IR.*;
import TEMP.*;
import TYPES.*;
import SYMBOL_TABLE.*;


public abstract class AST_VARDEC extends AST_Node
{
	public String type;
	public String name;
	public TYPE parsed_type;
	public Variable metadata;
	protected TEMP assignee;
	protected TEMP thisObj; // if the vardec is a cfield, this will be set.

	public AST_VARDEC(String type, String name) {
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.type = type;
		this.name = name;
		this.metadata = null;
		this.assignee = null;
	}

	public void setThisObject(TEMP obj) {
		this.thisObj = obj;
	}

	public TYPE SemantMe() throws AST_SEMANTIC_ERROR {
		SYMBOL_TABLE symbol_table = SYMBOL_TABLE.getInstance();
		TYPE type_target =  SYMBOL_TABLE.getInstance().findType(type);
		if (null == type_target) {
			reportSemanticError(String.format("Type %s does not exist", type));
//			System.exit(0);
			return null;
		}

		//CHECK IF NAME EXISTS IN SCOPE - class/function/variable with the same name - ERROR!
		if (null != SYMBOL_TABLE.getInstance().findInScope(name)) {
			reportSemanticError(String.format("name %s already defined within scope", name));
//			System.exit(0);
			return null;
		}

		TYPE name_t= SYMBOL_TABLE.getInstance().findTypeAndVoid(name);
		if(name_t != null) {
			reportSemanticError(String.format("name %s already defined as type/ class/ array name", name));
			return null;
		}

		// this happens only here for globals, locals, and class vars
		// the symbol table handles the addition to whoever needs it added.
		this.metadata = symbol_table.enterVariable(name, type_target);

		//todo: maybe remove
		this.parsed_type = type_target;

		//done!
		return type_target;
	}

	private TEMP getAssigneeDefaultValue() {
		// the IRMe function of a vardec should either return a temp containing the default value
		// or return a temp containing the assigned value.
		if (parsed_type instanceof TYPE_STRING) {
			return (TEMP_FACTORY.getInstance().getImmediateTEMP(
					IR_DATA_SECTION.getInstance().LabelNameForExpression(TYPE_STRING.EMPTY_STRING)));
		} else {
			return TEMP_FACTORY.getInstance().getNilTemp();
		}
	}

	// note the difference between this and the AST_VAR assignments. here we know for a fact that the storage exists,
	// since it's a var declaration.
	@Override
	public TEMP IRme() {
		if (this.assignee == null) {
			assignee = getAssigneeDefaultValue();
		}
		// this is the first time that the variable appeared, so we need to store into it.
		// this cannot beq a field inside, it's a declaration, thus we don't need to return a temp either.
		if (thisObj == null)
			IR.getInstance().Add_IRCommands( metadata.storeFrom(assignee));
		else
			IR.getInstance().Add_IRCommands( metadata.storeFrom(assignee, thisObj));


		return null;
	}
}
