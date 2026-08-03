package com.algoprep.patterns.math;

import java.util.HashMap;
import java.util.Map;

/** Checks whether four points form a non-degenerate square. */
public final class ValidSquare { private ValidSquare() {} public static boolean validSquare(int[] p1,int[] p2,int[] p3,int[] p4){int[][] p={p1,p2,p3,p4};Map<Long,Integer> counts=new HashMap<>();for(int i=0;i<4;i++)for(int j=i+1;j<4;j++)counts.merge(distance(p[i],p[j]),1,Integer::sum);return counts.size()==2&&counts.getOrDefault(0L,0)==0&&counts.containsValue(4)&&counts.containsValue(2);}private static long distance(int[] a,int[] b){long dx=(long)a[0]-b[0],dy=(long)a[1]-b[1];return dx*dx+dy*dy;} }
