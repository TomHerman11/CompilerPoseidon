package IR;

import SYMBOL_TABLE.SYMBOL_TABLE;

public class IR_OBJECT_MODEL {
    static IR_OBJECT_MODEL instance;
    public static IR_OBJECT_MODEL getInstance() throws Exception {
        if (instance == null) {
            throw new Exception("Kindly create the ir object model prior to getting instance");
        }
        return instance;
    }
    public static void createFrom(SYMBOL_TABLE symbol_table) {
        // foreach class type in symbol table:
        // if extends, do later
        // if basic:
        //     create list of data members (not function)
        //     create list of functions

        // for all extending:
        // add list of father class recursively
        // place override functions in vtable correctly
        // add new functions to list
        // add new memebers to list
    }

    /*


class {
    int a
    int b
    func goo
    func bar
    func c
.data
aHello .asciiz "Hello"
object_a_vtable
    dd offset foo
    dd offset bar

object_b_vtable
    dd offset fobar
    dd offset foo
    dd offset bar


.text
foo:
<code>
...
ret
bar:

<code>
...
ret



main:
    ; new a
    a = alloc(sizeof(a))
    memset(a, 0, sizeof(a))
    store(a, object_a_vtable)

    ;call foo
    vtable = load(a)
    fooraddr = load(vtalbe+ 4)


    call bar
    exit()
     */
}
