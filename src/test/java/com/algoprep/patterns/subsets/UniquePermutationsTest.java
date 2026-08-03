package com.algoprep.patterns.subsets;
import static org.junit.jupiter.api.Assertions.*; import org.junit.jupiter.api.Test;
class UniquePermutationsTest { @Test void excludesDuplicatePermutations() { var actual = UniquePermutations.permuteUnique(new int[] {1, 1, 2}); assertEquals(3, actual.size()); assertTrue(actual.containsAll(java.util.List.of(java.util.List.of(1,1,2), java.util.List.of(1,2,1), java.util.List.of(2,1,1)))); } @Test void emptyArrayHasEmptyPermutation() { assertEquals(java.util.List.of(java.util.List.of()), UniquePermutations.permuteUnique(new int[] {})); } }
