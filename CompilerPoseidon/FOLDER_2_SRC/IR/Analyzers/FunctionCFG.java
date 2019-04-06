package IR.Analyzers;

import IR.*;

import java.util.*;

public class FunctionCFG {
    static public class CFGNode {
        List<IRcommand> block;
        Set<CFGNode> inNodes, outNodes;

        CFGNode(List<IRcommand> block, Set<CFGNode> inNodes, Set<CFGNode> outNodes) {
            this.block = block;
            this.inNodes = inNodes;
            this.outNodes = outNodes;
        }

        Set<CFGNode> inNodes() { return inNodes; }
        Set<CFGNode> outNodes() { return outNodes; }
    }

    CFGNode start;
    protected FunctionCFG() { }


    private static class ShouldAddToNodeVisitor {
        public boolean visit(IRcommand c) { return true; }
        public boolean visit(IRcommand_Label lab) {
            return false;
        }
    }

    private static class ShouldCommitBlock {
        public boolean visit(IRcommand c) {return false;}
        public boolean visit(IRcommand_Label c) {return true;}
        public boolean visit(IRcommand_Jump_Label c) {return true;}
        public boolean visit(IRcommand_Cond_Jump c) {return true;}
    }

    /**
     * scan the function from start to end, split it into basic blcks, each basic block gets a node.
     * @param start
     * @param end
     * @return
     */
    public static FunctionCFG fromFunctionMarkers(ListIterator<IRcommand> start, ListIterator<IRcommand> end) {
        final int BASIC_BLOCK_INITIAL_SIZE = 4;
        FunctionCFG out = new FunctionCFG();
        ArrayList<IRcommand> basicBlock = new ArrayList<>(BASIC_BLOCK_INITIAL_SIZE); // assuming a BB is at least 4.
        ShouldCommitBlock committer = new ShouldCommitBlock();
        ShouldAddToNodeVisitor adder = new ShouldAddToNodeVisitor();
        while (!start.equals(end) ) {
            assert start.nextIndex() != end.nextIndex(); // if this is true then we need to replace the loop condition...
            IRcommand current = start.next();
            if (adder.visit(current)) basicBlock.add(current);
            if (committer.visit(current)) {
                out.commitBasicBlock(basicBlock);
                basicBlock = new ArrayList<IRcommand>(BASIC_BLOCK_INITIAL_SIZE);
                // if we didn't add the command to the previous block - we're adding it to this block.
                if (!adder.visit(current)) basicBlock.add(current);
            }
        }
        return out;
    }

    private void commitBasicBlock(ArrayList<IRcommand> basicBlock) {
        if (basicBlock == null || basicBlock.size() == 0) return; // probably the first label in the function, return.
        // todo: look for CFGNodes that end with a branch to the start label
        // todo: look for CFGNodes that start with the label that this jumps to in the end (if applicable)
        // todo: add as an outNode for the last CFGNode if it does not contain an absolute branch.
    }

}
