package com.algoprep.patterns.treebfs;
import static org.junit.jupiter.api.Assertions.*; import com.algoprep.common.TreeNode; import java.util.List; import org.junit.jupiter.api.Test;
class VerticalOrderTest { @Test void ordersByColumnRowThenValue(){assertEquals(List.of(List.of(9),List.of(3,15),List.of(20),List.of(7)),VerticalOrder.verticalTraversal(TreeNode.of(3,9,20,null,null,15,7)));} @Test void sortsTiedPositions(){assertEquals(List.of(List.of(2),List.of(1,4,5),List.of(3)),VerticalOrder.verticalTraversal(TreeNode.of(1,2,3,null,4,5,null)));} }
