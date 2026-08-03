package com.algoprep.patterns.topo;

/** Verifies lexicographic order using a supplied alphabet ordering. */
public final class VerifyAlienDictionary {
  private VerifyAlienDictionary() {}

  public static boolean isAlienSorted(String[] words, String order) {
    if (words == null || order == null) throw new IllegalArgumentException("inputs must not be null");
    int[] rank = new int[Character.MAX_VALUE + 1];
    for (int i = 0; i < order.length(); i++) rank[order.charAt(i)] = i;
    for (int i = 1; i < words.length; i++) {
      if (!inOrder(words[i - 1], words[i], rank)) return false;
    }
    return true;
  }

  private static boolean inOrder(String first, String second, int[] rank) {
    int length = Math.min(first.length(), second.length());
    for (int i = 0; i < length; i++) {
      if (first.charAt(i) != second.charAt(i)) {
        return rank[first.charAt(i)] < rank[second.charAt(i)];
      }
    }
    return first.length() <= second.length();
  }
}
