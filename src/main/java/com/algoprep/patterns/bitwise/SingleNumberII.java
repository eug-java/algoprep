package com.algoprep.patterns.bitwise;

/** Finds the value that occurs once when all others occur three times. */
public final class SingleNumberII { private SingleNumberII() {} public static int singleNumber(int[] nums) { int ones=0,twos=0;for(int n:nums){ones=(ones^n)&~twos;twos=(twos^n)&~ones;}return ones;} }
