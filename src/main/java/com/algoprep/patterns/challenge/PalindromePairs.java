package com.algoprep.patterns.challenge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Find all index pairs (i, j) such that words[i] + words[j] is a palindrome. */
public final class PalindromePairs {
  private PalindromePairs() {}

  public static List<List<Integer>> palindromePairs(String[] words) {
    List<List<Integer>> out = new ArrayList<>();
    if (words == null || words.length < 2) return out;
    Map<String, Integer> index = new HashMap<>();
    for (int i = 0; i < words.length; i++) index.put(words[i], i);
    Set<String> seen = new HashSet<>();
    for (int i = 0; i < words.length; i++) {
      String word = words[i];
      for (int cut = 0; cut <= word.length(); cut++) {
        String left = word.substring(0, cut);
        String right = word.substring(cut);
        if (isPalindrome(left)) {
          String rev = new StringBuilder(right).reverse().toString();
          Integer j = index.get(rev);
          if (j != null && j != i) add(out, seen, j, i);
        }
        if (cut != word.length() && isPalindrome(right)) {
          String rev = new StringBuilder(left).reverse().toString();
          Integer j = index.get(rev);
          if (j != null && j != i) add(out, seen, i, j);
        }
      }
    }
    return out;
  }

  private static void add(List<List<Integer>> out, Set<String> seen, int a, int b) {
    String key = a + "," + b;
    if (seen.add(key)) out.add(List.of(a, b));
  }

  private static boolean isPalindrome(String s) {
    int l = 0, r = s.length() - 1;
    while (l < r) {
      if (s.charAt(l++) != s.charAt(r--)) return false;
    }
    return true;
  }
}
