package IR.Analyzers;

import IR.IRFunctionStart;
import IR.IRcommand;
import IR.IRcommand_FunctionEnd;

import java.util.List;
import java.util.ListIterator;

abstract public class FunctionByFunctionAnalyzer extends AnalyzerBase {
    @Override
    public void analyze(List<IRcommand> commands) {
        ListIterator<IRcommand> start = null;
        ListIterator<IRcommand> it = commands.listIterator();
        while(it.hasNext()) {
            IRcommand cmd = it.next();
            if (cmd instanceof IRFunctionStart) {
                start = commands.listIterator(it.previousIndex());
            }
            if (cmd instanceof IRcommand_FunctionEnd) {
                analyzeFunction(start, it);
                start = null;
            }
        }
    }
    abstract protected void analyzeFunction(ListIterator<IRcommand> funcStart, ListIterator<IRcommand> end);

}
