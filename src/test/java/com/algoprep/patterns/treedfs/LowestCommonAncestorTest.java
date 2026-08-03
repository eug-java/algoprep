package com.algoprep.patterns.treedfs;
import static org.junit.jupiter.api.Assertions.*; import com.algoprep.common.TreeNode; import org.junit.jupiter.api.Test;
class LowestCommonAncestorTest { @Test void findsSplitAncestor(){TreeNode root=TreeNode.of(3,5,1,6,2,0,8,null,null,7,4);assertSame(root.left,LowestCommonAncestor.lowestCommonAncestor(root,root.left,root.left.right.right));} @Test void acceptsNullRoot(){assertNull(LowestCommonAncestor.lowestCommonAncestor(null,new TreeNode(1),new TreeNode(2)));} }
