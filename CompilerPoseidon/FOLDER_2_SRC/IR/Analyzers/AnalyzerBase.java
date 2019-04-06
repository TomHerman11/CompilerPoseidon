package IR.Analyzers;

import IR.*;

import java.util.List;
import java.util.ListIterator;

abstract public class AnalyzerBase {
    public AnalyzerBase() {}
    public void analyze(List<IRcommand> commands) {
        ListIterator<IRcommand> start = null;
        ListIterator<IRcommand> it = commands.listIterator();
        while(it.hasNext()) {
            IRcommand cmd = it.next();
            if (cmd instanceof IRFunctionStart) {
                start = it;
            }
            if (cmd instanceof IRcommand_FunctionEnd) {
                analyzeFunction(start, it);
                start = null;
            }
        }
    }
    abstract protected void analyzeFunction(ListIterator<IRcommand> start, ListIterator<IRcommand> end);
}
