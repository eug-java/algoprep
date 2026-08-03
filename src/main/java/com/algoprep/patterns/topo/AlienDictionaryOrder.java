package com.algoprep.patterns.topo;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Derives an alphabetical order from sorted alien-language words. */
public final class AlienDictionaryOrder {
  private AlienDictionaryOrder() {}
  public static String alienOrder(String[] words) {
    Map<Character, Set<Character>> graph = new HashMap<>(); Map<Character, Integer> degree = new HashMap<>();
    for (String word : words) for (char c : word.toCharArray()) { graph.putIfAbsent(c, new HashSet<>()); degree.putIfAbsent(c, 0); }
    for (int i = 0; i + 1 < words.length; i++) { String first = words[i], second = words[i + 1]; if (first.length() > second.length() && first.startsWith(second)) return ""; int limit = Math.min(first.length(), second.length()); for (int j = 0; j < limit; j++) if (first.charAt(j) != second.charAt(j)) { if (graph.get(first.charAt(j)).add(second.charAt(j))) degree.merge(second.charAt(j), 1, Integer::sum); break; } }
    ArrayDeque<Character> queue = new ArrayDeque<>(); degree.forEach((c, count) -> { if (count == 0) queue.add(c); }); StringBuilder order = new StringBuilder(); while (!queue.isEmpty()) { char c = queue.remove(); order.append(c); for (char next : graph.get(c)) if (degree.merge(next, -1, Integer::sum) == 0) queue.add(next); } return order.length() == degree.size() ? order.toString() : "";
  }
}
