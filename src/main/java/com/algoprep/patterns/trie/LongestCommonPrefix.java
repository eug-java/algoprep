package com.algoprep.patterns.trie;

/** Finds the shared prefix among a set of strings using a vertical scan. */
public final class LongestCommonPrefix {
  private LongestCommonPrefix() {}

  public static String longestCommonPrefix(String[] strs) {
    if (strs == null || strs.length == 0) return "";
    if (strs[0] == null) throw new IllegalArgumentException("strings must not contain null");
    for (int index = 0; index < strs[0].length(); index++) {
      char expected = strs[0].charAt(index);
      for (int word = 1; word < strs.length; word++) {
        if (strs[word] == null) throw new IllegalArgumentException("strings must not contain null");
        if (index == strs[word].length() || strs[word].charAt(index) != expected) {
          return strs[0].substring(0, index);
        }
      }
    }
    return strs[0];
  }
}
