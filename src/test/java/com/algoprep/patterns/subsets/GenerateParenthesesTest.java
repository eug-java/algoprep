package com.algoprep.patterns.subsets;
import static org.junit.jupiter.api.Assertions.*; import org.junit.jupiter.api.Test;
class GenerateParenthesesTest { @Test void generatesBalancedPairs() { assertEquals(java.util.Set.of("((()))", "(()())", "(())()", "()(())", "()()()"), new java.util.HashSet<>(GenerateParentheses.generateParenthesis(3))); } @Test void handlesZeroPairs() { assertEquals(java.util.List.of(""), GenerateParentheses.generateParenthesis(0)); } }
