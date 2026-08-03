package com.algoprep.patterns.stacks;

import java.util.ArrayDeque;

/** Evaluates expressions containing addition, subtraction, and parentheses. */
public final class BasicCalculator {
  private BasicCalculator() {}
  public static int calculate(String s) { int result = 0, number = 0, sign = 1; ArrayDeque<Integer> stack = new ArrayDeque<>(); for (char c : s.toCharArray()) { if (Character.isDigit(c)) number = number * 10 + c - '0'; else if (c == '+' || c == '-') { result += sign * number; number = 0; sign = c == '+' ? 1 : -1; } else if (c == '(') { stack.push(result); stack.push(sign); result = 0; sign = 1; } else if (c == ')') { result += sign * number; number = 0; result *= stack.pop(); result += stack.pop(); } } return result + sign * number; }
}
