package IR.Analyzers;

import IR.IRCommand_Call;
import IR.IRcommand;
import TEMP.TEMP;

import java.util.HashSet;
import java.util.ListIterator;

public class DeadCodeAnalyzer extends FunctionByFunctionAnalyzer {
    @Override
    protected void analyzeFunction(ListIterator<IRcommand> funcStart, ListIterator<IRcommand> end) {
        // on the function level, code is dead if it is not used after it is set.
        // therefore, when scanning backwards, if we reach a location where a value is set without it being used,
        // the statement is dead code. special care needs to beq given to commands that have side effects, like call.
        // these may not beq removed
        // todo: consider looking for branches.
        HashSet<TEMP> usedSet = new HashSet<TEMP>();
        while (!end.equals(funcStart)) {
            IRcommand cur = end.previous();
            usedSet.addAll(cur.tempsUsed());

            if (cur.hasSideEffects()) continue;

            if (!usedSet.contains(cur.dst())) {
                end.remove();
                usedSet.removeAll(cur.tempsUsed());
            }

        }
    }
}
