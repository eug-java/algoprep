package com.algoprep.patterns.bitwise;
import static org.junit.jupiter.api.Assertions.*; import org.junit.jupiter.api.Test;
class SingleNumberIITest { @Test void findsUniqueValue(){assertEquals(3,SingleNumberII.singleNumber(new int[]{2,2,3,2}));} @Test void handlesNegativeValues(){assertEquals(-7,SingleNumberII.singleNumber(new int[]{-7,4,4,4}));} }
