package com.algoprep.patterns.hashmaps;

import java.util.HashMap;
import java.util.Map;

/** Checks whether two strings have a one-to-one character mapping. */
public final class IsomorphicStrings {
  private IsomorphicStrings() {}
  public static boolean isIsomorphic(String s, String t) {
    if (s.length() != t.length()) return false;
    Map<Character, Character> forward = new HashMap<>(), reverse = new HashMap<>();
    for (int i = 0; i < s.length(); i++) { char a=s.charAt(i), b=t.charAt(i); if ((forward.containsKey(a) && forward.get(a) != b) || (reverse.containsKey(b) && reverse.get(b) != a)) return false; forward.put(a,b); reverse.put(b,a); }
    return true;
  }
}
