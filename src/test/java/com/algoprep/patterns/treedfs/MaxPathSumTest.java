package com.algoprep.patterns.treedfs;
import static org.junit.jupiter.api.Assertions.*; import com.algoprep.common.TreeNode; import org.junit.jupiter.api.Test;
class MaxPathSumTest { @Test void crossesRootWhenOptimal(){assertEquals(42,MaxPathSum.maxPathSum(TreeNode.of(-10,9,20,null,null,15,7)));} @Test void handlesAllNegativeTree(){assertEquals(-3,MaxPathSum.maxPathSum(TreeNode.of(-3)));} }
