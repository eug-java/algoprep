package com.algoprep.patterns.stacks;
import static org.junit.jupiter.api.Assertions.*; import org.junit.jupiter.api.Test;
class BasicCalculatorTest { @Test void evaluatesNestedParentheses() { assertEquals(23, BasicCalculator.calculate("(1+(4+5+2)-3)+(6+8)")); } @Test void handlesWhitespaceAndSubtraction() { assertEquals(3, BasicCalculator.calculate(" 2-1 + 2 ")); } }
