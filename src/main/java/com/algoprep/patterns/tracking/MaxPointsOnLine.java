package com.algoprep.patterns.tracking;

import java.util.HashMap;
import java.util.Map;

/** Finds the largest number of collinear points. */
public final class MaxPointsOnLine {
  private MaxPointsOnLine() {}
  public static int maxPoints(int[][] points) {
    if (points.length <= 2) return points.length; int answer=0;
    for(int i=0;i<points.length;i++){ Map<String,Integer> slopes=new HashMap<>();int duplicates=1,best=0; for(int j=i+1;j<points.length;j++){int dx=points[j][0]-points[i][0],dy=points[j][1]-points[i][1];if(dx==0&&dy==0){duplicates++;continue;}int g=gcd(dx,dy);dx/=g;dy/=g;if(dx<0){dx=-dx;dy=-dy;} else if(dx==0)dy=1; else if(dy==0)dx=1;String key=dy+"/"+dx;int count=slopes.merge(key,1,Integer::sum);best=Math.max(best,count);} answer=Math.max(answer,best+duplicates); } return answer;
  }
  private static int gcd(int a,int b){a=Math.abs(a);b=Math.abs(b);while(b!=0){int t=a%b;a=b;b=t;}return a;}
}
