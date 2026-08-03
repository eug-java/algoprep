package com.algoprep.patterns.stacks;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

/** Validates properly nested parentheses, brackets, and braces. */
public final class ValidParentheses {
  private ValidParentheses() {}

  public static boolean isValid(String s) {
    Map<Character, Character> opening = Map.of(')', '(', ']', '[', '}', '{');
    Deque<Character> stack = new ArrayDeque<>();
    for (char character : s.toCharArray()) {
      if (opening.containsKey(character)) {
        if (stack.isEmpty() || stack.pop() != opening.get(character)) return false;
      } else {
        stack.push(character);
      }
    }
    return stack.isEmpty();
  }
}
