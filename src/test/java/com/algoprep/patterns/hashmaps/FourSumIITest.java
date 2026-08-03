package com.algoprep.patterns.hashmaps;
import static org.junit.jupiter.api.Assertions.*; import org.junit.jupiter.api.Test;
class FourSumIITest { @Test void countsAllZeroTuples(){assertEquals(2,FourSumII.fourSumCount(new int[]{1,2},new int[]{-2,-1},new int[]{-1,2},new int[]{0,2}));} @Test void countsDuplicateCombinations(){assertEquals(16,FourSumII.fourSumCount(new int[]{0,0},new int[]{0,0},new int[]{0,0},new int[]{0,0}));} }
