package com.algoprep.patterns.cyclicsort;
import static org.junit.jupiter.api.Assertions.*; import org.junit.jupiter.api.Test;
class FirstMissingPositiveTest { @Test void findsInternalGap() { assertEquals(2, FirstMissingPositive.firstMissingPositive(new int[] {3,4,-1,1})); } @Test void findsNextAfterRange() { assertEquals(4, FirstMissingPositive.firstMissingPositive(new int[] {1,2,3})); } }
