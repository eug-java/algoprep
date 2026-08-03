package com.algoprep.patterns.trie;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Replaces words with their shortest dictionary root. */
public final class ReplaceWords {
  private ReplaceWords() {}
  public static String replaceWords(List<String> dictionary, String sentence) {
    Set<String> roots = new HashSet<>(dictionary); String[] words = sentence.split(" ");
    for (int i = 0; i < words.length; i++) {
      for (int end = 1; end <= words[i].length(); end++) {
        String prefix = words[i].substring(0, end);
        if (roots.contains(prefix)) { words[i] = prefix; break; }
      }
    }
    return String.join(" ", words);
  }
}
