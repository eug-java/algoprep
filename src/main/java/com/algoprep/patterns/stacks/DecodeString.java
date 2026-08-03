package com.algoprep.patterns.stacks;

import java.util.ArrayDeque;

/** Decodes bracketed repetition expressions. */
public final class DecodeString {
  private DecodeString() {}
  public static String decodeString(String s) { ArrayDeque<Integer> counts = new ArrayDeque<>(); ArrayDeque<StringBuilder> prefixes = new ArrayDeque<>(); StringBuilder current = new StringBuilder(); int number = 0; for (char c : s.toCharArray()) { if (Character.isDigit(c)) number = number * 10 + c - '0'; else if (c == '[') { counts.push(number); prefixes.push(current); number = 0; current = new StringBuilder(); } else if (c == ']') { String repeated = current.toString().repeat(counts.pop()); current = prefixes.pop().append(repeated); } else current.append(c); } return current.toString(); }
}
