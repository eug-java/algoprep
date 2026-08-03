package com.algoprep.patterns.hashmaps;
import static org.junit.jupiter.api.Assertions.*; import org.junit.jupiter.api.Test;
class ContiguousArrayTest { @Test void findsLongestBalancedRange(){assertEquals(4,ContiguousArray.findMaxLength(new int[]{0,1,0,1,1}));} @Test void returnsZeroWhenNoBalance(){assertEquals(0,ContiguousArray.findMaxLength(new int[]{1,1}));} }
