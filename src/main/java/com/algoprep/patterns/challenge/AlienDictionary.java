package com.algoprep.patterns.challenge;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/** Infers one valid character order from lexicographically sorted alien words. */
public final class AlienDictionary {
  private AlienDictionary() {}

  public static String alienOrder(String[] words) {
    if (words == null || words.length == 0) {
      return "";
    }

    Map<Character, Set<Character>> graph = new HashMap<>();
    Map<Character, Integer> indegrees = new HashMap<>();
    for (String word : words) {
      if (word == null) {
        return "";
      }
      for (char character : word.toCharArray()) {
        graph.putIfAbsent(character, new HashSet<>());
        indegrees.putIfAbsent(character, 0);
      }
    }

    for (int i = 0; i < words.length - 1; i++) {
      String first = words[i];
      String second = words[i + 1];
      if (first.length() > second.length() && first.startsWith(second)) {
        return "";
      }
      for (int j = 0; j < Math.min(first.length(), second.length()); j++) {
        char before = first.charAt(j);
        char after = second.charAt(j);
        if (before != after) {
          if (graph.get(before).add(after)) {
            indegrees.put(after, indegrees.get(after) + 1);
          }
          break;
        }
      }
    }

    Queue<Character> sources = new ArrayDeque<>();
    for (Map.Entry<Character, Integer> entry : indegrees.entrySet()) {
      if (entry.getValue() == 0) {
        sources.add(entry.getKey());
      }
    }

    StringBuilder order = new StringBuilder();
    while (!sources.isEmpty()) {
      char character = sources.remove();
      order.append(character);
      for (char next : graph.get(character)) {
        int degree = indegrees.merge(next, -1, Integer::sum);
        if (degree == 0) {
          sources.add(next);
        }
      }
    }
    return order.length() == indegrees.size() ? order.toString() : "";
  }
}
