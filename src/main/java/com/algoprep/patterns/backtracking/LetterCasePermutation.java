package com.algoprep.patterns.backtracking;

import java.util.ArrayList;
import java.util.List;

/** Generates all strings obtainable by independently changing letter case. */
public final class LetterCasePermutation {
  private LetterCasePermutation() {}

  public static List<String> letterCasePermutation(String s) {
    if (s == null) throw new IllegalArgumentException("s must not be null");
    List<String> permutations = new ArrayList<>();
    char[] characters = s.toCharArray();
    backtrack(characters, 0, permutations);
    return permutations;
  }

  private static void backtrack(char[] characters, int index, List<String> permutations) {
    if (index == characters.length) {
      permutations.add(new String(characters));
      return;
    }
    char character = characters[index];
    if (!Character.isLetter(character)) {
      backtrack(characters, index + 1, permutations);
      return;
    }
    characters[index] = Character.toLowerCase(character);
    backtrack(characters, index + 1, permutations);
    characters[index] = Character.toUpperCase(character);
    backtrack(characters, index + 1, permutations);
    characters[index] = character;
  }
}
