package com.algoprep.patterns.trie;

import java.util.HashMap;
import java.util.Map;

/** Prefix tree supporting word and prefix lookups. */
public class Trie {
  private final Node root = new Node();

  public void insert(String word) {
    Node node = root;
    for (char ch : word.toCharArray())
      node = node.children.computeIfAbsent(ch, ignored -> new Node());
    node.word = true;
  }

  public boolean search(String word) {
    Node node = find(word);
    return node != null && node.word;
  }

  public boolean startsWith(String prefix) {
    return find(prefix) != null;
  }

  private Node find(String text) {
    Node node = root;
    for (char ch : text.toCharArray()) {
      node = node.children.get(ch);
      if (node == null) return null;
    }
    return node;
  }

  private static class Node {
    Map<Character, Node> children = new HashMap<>();
    boolean word;
  }
}
