package com.algoprep.patterns.stacks;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ValidParenthesesTest {
  @Test
  void validatesNestedAndInvalidSequences() {
    assertTrue(ValidParentheses.isValid("()[]{}"));
    assertTrue(ValidParentheses.isValid("{[]}"));
    assertFalse(ValidParentheses.isValid("(]"));
    assertFalse(ValidParentheses.isValid("([)]"));
    assertFalse(ValidParentheses.isValid("("));
  }
}
