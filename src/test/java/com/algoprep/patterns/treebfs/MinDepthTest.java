package com.algoprep.patterns.treebfs;
import static org.junit.jupiter.api.Assertions.*; import com.algoprep.common.TreeNode; import org.junit.jupiter.api.Test;
class MinDepthTest { @Test void findsNearestLeaf(){assertEquals(3,MinDepth.minDepth(TreeNode.of(1,2,3,4,null,null,5)));} @Test void handlesEmptyTree(){assertEquals(0,MinDepth.minDepth(null));} }
