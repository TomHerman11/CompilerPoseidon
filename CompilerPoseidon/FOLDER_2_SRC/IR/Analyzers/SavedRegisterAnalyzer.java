package IR.Analyzers;

import IR.IRcommand;
import IR.IRcommand_FunctionEpilogue;
import IR.IRcommand_FunctionPrologue;
import IR.RegisterBoundry;
import TEMP.TEMP;
import TEMP.REGISTER;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

import static IR.RegisterBoundry.BoundryType.interFunction;

public class SavedRegisterAnalyzer extends FunctionByFunctionAnalyzer {
    private Set<TEMP.REGISTER_NAME> trashed;
    private Set<REGISTER> argsUsed; // if we use 'em: 1. they are args passed to us, and 2. we restore them.
    ArrayList<RegisterBoundry> savers;
    @Override
    protected void analyzeFunction(ListIterator<IRcommand> funcStart, ListIterator<IRcommand> end) {
        trashed = new HashSet<>();
        argsUsed = new HashSet<REGISTER>();
        savers = new ArrayList<>();
        // look for all the temp regs that are used in the function.
        // tell every call to save them
        // tell the prologue and epilogue to save their corresponding saved reg.
        while (funcStart.nextIndex() != end.nextIndex()) {
            IRcommand cmd = funcStart.next();
            if (cmd.dst() != null) {
                if (cmd.dst().register().temp()) {
                    trashed.add(cmd.dst().register());
                }
            }
            if (cmd.tempsUsed() != null) {
                for (TEMP i : cmd.tempsUsed()) {
                    if (i.register().arg()) {
                        argsUsed.add((REGISTER)i);
                    }
                }
            }
            if (cmd instanceof RegisterBoundry) {
                savers.add((RegisterBoundry)cmd);
            }
        }
        // now - a function call uses trashed.size() saved registers to save the current state.
        // we need to save the first trashed.size() saved registers in the prologue and restore in epilogue.
        // however - if there is no function call, we don't need to do anything.
        if (savers.size() < 3) return; // there is a prologue and an epilogue and that's it.
        setSaved();

    }

    private void setSaved() {
        Set<REGISTER> functionSaved = new HashSet<>();
        Set<REGISTER> regs = new HashSet<>();

        for (int i = 0 ; i < trashed.size(); ++i) functionSaved.add(REGISTER.saved(i));
        for (TEMP.REGISTER_NAME n: trashed) regs.add(REGISTER.fromName(n));
        // todo: make sure that IRconstructorStart/end gets the saved args as well
        for (RegisterBoundry saver : savers) {
            switch(saver.boundryType()) {
                case interFunction:
                    saver.saveRegisters(functionSaved);
                    break;
                case local:
                    saver.saveRegisters(regs);
                    break;
                case args:
                    saver.saveRegisters(argsUsed);
                    break;
            }
        }
    }
}
