package com.algoprep.patterns.bitwise;

/** Reverses all 32 bits of an integer. */
public final class ReverseBits { private ReverseBits() {} public static int reverseBits(int n){int result=0;for(int i=0;i<32;i++){result=(result<<1)|(n&1);n>>>=1;}return result;} }
