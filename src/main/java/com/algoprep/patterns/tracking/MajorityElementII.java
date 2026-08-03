package com.algoprep.patterns.tracking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Finds elements occurring more than n/3 times. */
public final class MajorityElementII {
  private MajorityElementII() {}
  public static List<Integer> majorityElement(int[] nums) {
    int a=0,b=1,ca=0,cb=0;
    for(int n:nums) { if(n==a) ca++; else if(n==b) cb++; else if(ca==0){a=n;ca=1;} else if(cb==0){b=n;cb=1;} else {ca--;cb--;} }
    ca=0;cb=0;for(int n:nums){if(n==a)ca++;else if(n==b)cb++;} List<Integer> result=new ArrayList<>(); if(ca>nums.length/3)result.add(a);if(cb>nums.length/3)result.add(b);Collections.sort(result);return result;
  }
}
