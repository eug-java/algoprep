package com.algoprep.patterns.stacks;
import static org.junit.jupiter.api.Assertions.*; import org.junit.jupiter.api.Test;
class DecodeStringTest { @Test void decodesNestedRepeats() { assertEquals("accaccacc", DecodeString.decodeString("3[a2[c]]")); } @Test void supportsMultiDigitCounts() { assertEquals("aaaaaaaaaa", DecodeString.decodeString("10[a]")); } }
