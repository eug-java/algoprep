package com.algoprep.patterns.math;

/** Calculates the floor of a non-negative integer square root. */
public final class SqrtX { private SqrtX() {} public static int mySqrt(int x){if(x<2)return x;int low=1,high=x/2,answer=0;while(low<=high){int mid=low+(high-low)/2;if(mid<=x/mid){answer=mid;low=mid+1;}else high=mid-1;}return answer;} }
