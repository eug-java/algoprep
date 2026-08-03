package com.algoprep.patterns.bitwise;
import static org.junit.jupiter.api.Assertions.*; import org.junit.jupiter.api.Test;
class ReverseBitsTest { @Test void reversesAllBits(){assertEquals(964176192,ReverseBits.reverseBits(43261596));} @Test void handlesHighBit(){assertEquals(1,ReverseBits.reverseBits(Integer.MIN_VALUE));} }
