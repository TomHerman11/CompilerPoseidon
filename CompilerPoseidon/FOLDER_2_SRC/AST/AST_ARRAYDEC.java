package AST;

import SYMBOL_TABLE.SYMBOL_TABLE;
import TYPES.TYPE;
import TYPES.TYPE_ARRAY;
import TYPES.TYPE_VOID;

public  class AST_ARRAYDEC extends AST_Node
{
    public String name;
    public String type;

    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public AST_ARRAYDEC(String name ,String type)
    {
        /******************************/
        /* SET A UNIQUE SERIAL NUMBER */
        /******************************/
        SerialNumber = AST_Node_Serial_Number.getFresh();

        /***************************************/
        /* PRINT CORRESPONDING DERIVATION RULE */
        /***************************************/
        System.out.format("====================== ArrayDec-> simple\n");

        /*******************************/
        /* COPY INPUT DATA NENBERS ... */
        /*******************************/
        this.name	= name;
        this.type	= type;
    }

    /************************************************/
    /* The printing message for an INT EXP AST node */
    /************************************************/
    public void PrintMe()
    {
        /*******************************/
        /* AST NODE TYPE = AST INT EXP */
        /*******************************/
        System.out.format("AST NODE Arraydec_simple\n" + name + "\n" + type);

        /*********************************/
        /* Print to AST GRAPHIZ DOT file */
        /*********************************/
        AST_GRAPHVIZ.getInstance().logNode(
                SerialNumber,
                String.format("Arraydec_simple" + name + "\n" + type));
    }

    public TYPE SemantMe () throws AST_SEMANTIC_ERROR
    {
        TYPE t_array_name;
        TYPE t_array_type;

        //check that ARRAY is declared in GLOBAL SCOPE:
        if (!SYMBOL_TABLE.getInstance().is_global_scope()) {
            System.out.format(">> ERROR ARRAY %s mush beq declared in global scope\n",name);
            reportSemanticError(String.format(">> ERROR ARRAY %s mush beq declared in global scope\n",name));
        }


        //check that t_array_name DOES NOT EXIST IN SYMBOL TABLE
        t_array_name = SYMBOL_TABLE.getInstance().find(name);

        if (t_array_name != null)
        {
            System.out.format(">> ERROR ARRAY name %s DOES exist in the program\n", name);
            reportSemanticError(String.format(">> ERROR ARRAY name %s DOES  exist in the program\n",name));
        }

        //check the t_array_type DOES EXIST IN SYMBOL TABLE
        t_array_type = SYMBOL_TABLE.getInstance().findType(type);
        if (t_array_type == null)
        {
            System.out.format(">> ERROR TYPE %s DOES NOT exist in the program\n",type);
            reportSemanticError(String.format(">> ERROR TYPE %s DOES NOT exist in the program\n",type));
        }

        /****** already checked in previous "if" *******

         //check if it a valid type
         if (!t_array_type.name.equals(type)) {
         System.out.format(">> ERROR %s is not a valid type\n",type);
         reportSemanticError(String.format(">> ERROR %s is not a valid type\n",type));
         }

         ***********************************************/

        TYPE_ARRAY t = new TYPE_ARRAY(name, t_array_type);
        SYMBOL_TABLE.getInstance().enter(name, t);
        this.nodeWeight = 0; // leaf node
        //all is well
        return TYPE_VOID.getInstance();
    }
    /* ~~~~~~~~ No IRme Necessary ~~~~~~~~ */
}
