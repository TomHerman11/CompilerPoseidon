package SYMBOL_TABLE

//
//private AST_DEC_LIST globArrayDec(String name, String type) {
//    return new AST_DEC_LIST(
//            new AST_DEC_ARRAYDEC(new AST_ARRAYDEC_SIMPLE(name, type)),
//            null)
//}
//
//private AST_DEC_LIST globVarDec(String name, String type) {
//    return new AST_DEC_LIST(
//            new AST_DEC_VARDEC(new AST_VARDEC_SIMPLE(name, type)),
//            null)
//}
//
//private AST_STMT_LIST stmtList(AST_STMT something) {
//    return new AST_STMT_LIST(something, null)
//}
//
//
//private AST_DEC_LIST globFuncDec(String retType,
//                                 String funcName,
//                                 AST_ID_LIST args,
//                                 AST_STMT_LIST funcBody) {
//    return new AST_DEC_FUNCDEC(
//            new AST_FUNCDEC_SIMPLE(retType, funcName, args, funcBody);
//    )
//}

class SYMBOL_TABLETest extends GroovyTestCase {

    private <T, V > T listOf(Object... args) {
        return T.newInstance(Arrays.asList(V.newInstance(args), null)) as T;
    }

    private AST_PROGRAM mock_program
    void setUp() {
        super.setUp()
        mock_program = new AST_PROGRAM()

//        AST_DEC_LIST globalDecs = listOf< AST_DEC_LIST, AST_ARRAYDEC_SIMPLE>(
//                "type", "name"
//        )

//        globalDecs.tail =
//        mock_program.dec_list = new AST_DEC_LIST()

    }

    void testEnter() {
        AssertionError("something here")
    }

    void testEnterVariable() {
    }

    void testEnterArgument() {
    }

    void testEnterFunction() {
    }

    void testEnter1() {
    }

    void testFind() {
    }

    void testFindInScope() {
    }

    void testFindClassInSymbolTable() {
    }

    void testFindType() {
    }

    void testFindTypeAndVoid() {
    }

    void testFindArrayInSymbolTable() {
    }

    void testFindMetadata() {
    }

    void testBeginScope() {
    }

    void testBeginScope1() {
    }

    void testUpdateWithFatherClass() {
    }

    void testBeginScope2() {
    }

    void testGetScopeType() {
    }

    void testEndScope() {
    }

    void testEndScope1() {
    }

    void testEndScope2() {
    }

    void testPrintMe() {
    }

    void testGetInstance() {
    }

    void testIs_global_scope() {
    }

    void testGet_scope_number() {
    }

    void testIs_in_class() {
    }

    void testGetCurTypeClass() {
    }

    void testGetCurTypeFunction() {
    }
}
