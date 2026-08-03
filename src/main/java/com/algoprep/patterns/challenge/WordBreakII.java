package com.algoprep.patterns.challenge;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Returns every sentence formed by segmenting s using dictionary words. */
public final class WordBreakII {
  private WordBreakII() {}

  public static List<String> wordBreak(String s, List<String> wordDict) {
    if (s == null || s.isEmpty()) return List.of();
    Set<String> dict = new HashSet<>(wordDict == null ? List.of() : wordDict);
    List<String> out = new ArrayList<>();
    dfs(s, 0, dict, new ArrayList<>(), out);
    return out;
  }

  private static void dfs(String s, int start, Set<String> dict, List<String> path, List<String> out) {
    if (start == s.length()) {
      out.add(String.join(" ", path));
      return;
    }
    for (int end = start + 1; end <= s.length(); end++) {
      String word = s.substring(start, end);
      if (!dict.contains(word)) continue;
      path.add(word);
      dfs(s, end, dict, path, out);
      path.remove(path.size() - 1);
    }
  }
}
