package com.algoprep.patterns.trie;

import java.util.ArrayList;
import java.util.List;

/** Finds dictionary words constructible by adjacent board cells. */
public final class WordSearchII {
  private WordSearchII() {}
  public static List<String> findWords(char[][] board, String[] words) {
    Node root = new Node(); for (String word : words) insert(root, word);
    List<String> result = new ArrayList<>();
    for (int r = 0; r < board.length; r++) for (int c = 0; c < board[r].length; c++) search(board, r, c, root, result);
    return result;
  }
  private static void insert(Node root, String word) { for (char ch : word.toCharArray()) { int i = ch - 'a'; if (root.next[i] == null) root.next[i] = new Node(); root = root.next[i]; } root.word = word; }
  private static void search(char[][] b, int r, int c, Node node, List<String> out) {
    if (r < 0 || r == b.length || c < 0 || c == b[r].length || b[r][c] == '#') return;
    node = node.next[b[r][c] - 'a']; if (node == null) return;
    if (node.word != null) { out.add(node.word); node.word = null; }
    char ch = b[r][c]; b[r][c] = '#'; search(b,r-1,c,node,out); search(b,r+1,c,node,out); search(b,r,c-1,node,out); search(b,r,c+1,node,out); b[r][c] = ch;
  }
  private static final class Node { private final Node[] next = new Node[26]; private String word; }
}
