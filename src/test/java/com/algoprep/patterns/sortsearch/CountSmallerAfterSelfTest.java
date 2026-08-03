package com.algoprep.patterns.sortsearch;
import static org.junit.jupiter.api.Assertions.*; import org.junit.jupiter.api.Test;
class CountSmallerAfterSelfTest { @Test void countsSmallerValues() { assertEquals(java.util.List.of(2,1,1,0), CountSmallerAfterSelf.countSmaller(new int[] {5,2,6,1})); } @Test void handlesDuplicates() { assertEquals(java.util.List.of(0,0), CountSmallerAfterSelf.countSmaller(new int[] {2,2})); } }
