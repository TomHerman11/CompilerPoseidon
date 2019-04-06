package AST;

import static java.lang.String.format;

public class AST_SEMANTIC_ERROR extends Exception {
    int lineNumber;
    public AST_SEMANTIC_ERROR(String message, int line) {
        super(message);
        lineNumber = line;
    }

    public AST_SEMANTIC_ERROR(int line) {
        super(format("Error on line %d",(line)));
        lineNumber = line;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
