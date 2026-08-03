package com.algoprep.patterns.sortsearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Groups strings that contain the same letters. */
public final class GroupAnagrams {
  private GroupAnagrams() {}

  public static List<List<String>> group(String[] strs) {
    Map<String, List<String>> groups = new LinkedHashMap<>();
    for (String word : strs) {
      char[] letters = word.toCharArray();
      Arrays.sort(letters);
      groups.computeIfAbsent(new String(letters), ignored -> new ArrayList<>()).add(word);
    }
    return new ArrayList<>(groups.values());
  }
}
