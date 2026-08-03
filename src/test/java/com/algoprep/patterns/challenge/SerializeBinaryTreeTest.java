package com.algoprep.patterns.challenge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.algoprep.common.TreeNode;
import org.junit.jupiter.api.Test;

class SerializeBinaryTreeTest {
  private final SerializeBinaryTree codec = new SerializeBinaryTree();

  @Test
  void roundTripsAnAsymmetricTree() {
    TreeNode original = TreeNode.of(1, 2, 3, null, 4, null, 5);
    String encoded = codec.serialize(original);

    TreeNode restored = codec.deserialize(encoded);

    assertEquals(TreeNode.levelOrderValues(original), TreeNode.levelOrderValues(restored));
    assertNull(restored.left.left);
    assertEquals(4, restored.left.right.val);
    assertNull(restored.right.left);
  }

  @Test
  void roundTripsAnEmptyTree() {
    assertNull(codec.deserialize(codec.serialize(null)));
  }
}
