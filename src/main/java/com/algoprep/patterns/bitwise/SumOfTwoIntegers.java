package com.algoprep.patterns.bitwise;

/** Adds integers without arithmetic operators. */
public final class SumOfTwoIntegers { private SumOfTwoIntegers() {} public static int getSum(int a,int b){while(b!=0){int carry=(a&b)<<1;a^=b;b=carry;}return a;} }
