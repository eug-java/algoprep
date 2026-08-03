package com.algoprep.patterns.challenge;

import com.algoprep.common.TreeNode;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

/** Serializes binary trees in preorder with null markers for structural fidelity. */
public final class SerializeBinaryTree {
  private static final String NULL = "#";
  private static final String SEPARATOR = ",";

  public String serialize(TreeNode root) {
    StringBuilder encoded = new StringBuilder();
    serialize(root, encoded);
    return encoded.toString();
  }

  public TreeNode deserialize(String data) {
    if (data == null || data.isEmpty()) {
      return null;
    }
    Deque<String> values = new ArrayDeque<>(Arrays.asList(data.split(SEPARATOR)));
    TreeNode root = deserialize(values);
    if (!values.isEmpty()) {
      throw new IllegalArgumentException("Serialized tree contains extra values.");
    }
    return root;
  }

  private void serialize(TreeNode node, StringBuilder encoded) {
    if (node == null) {
      encoded.append(NULL).append(SEPARATOR);
      return;
    }
    encoded.append(node.val).append(SEPARATOR);
    serialize(node.left, encoded);
    serialize(node.right, encoded);
  }

  private TreeNode deserialize(Deque<String> values) {
    if (values.isEmpty()) {
      throw new IllegalArgumentException("Serialized tree ended unexpectedly.");
    }
    String value = values.removeFirst();
    if (NULL.equals(value)) {
      return null;
    }
    try {
      TreeNode node = new TreeNode(Integer.parseInt(value));
      node.left = deserialize(values);
      node.right = deserialize(values);
      return node;
    } catch (NumberFormatException exception) {
      throw new IllegalArgumentException("Serialized tree contains an invalid value.", exception);
    }
  }
}
