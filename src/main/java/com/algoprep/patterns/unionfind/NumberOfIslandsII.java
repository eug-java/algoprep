package com.algoprep.patterns.unionfind;

import java.util.ArrayList;
import java.util.List;

/** Tracks island counts as land positions are added. */
public final class NumberOfIslandsII {
  private NumberOfIslandsII() {}
  public static List<Integer> numIslands2(int m, int n, int[][] positions) {
    UnionFind uf=new UnionFind(m*n); boolean[] land=new boolean[m*n]; List<Integer> result=new ArrayList<>(); int count=0; int[][] dirs={{1,0},{-1,0},{0,1},{0,-1}};
    for(int[] p:positions){int r=p[0],c=p[1],id=r*n+c;if(land[id]){result.add(count);continue;}land[id]=true;count++;for(int[] d:dirs){int nr=r+d[0],nc=c+d[1];if(nr>=0&&nr<m&&nc>=0&&nc<n&&land[nr*n+nc]&&uf.union(id,nr*n+nc))count--;}result.add(count);}return result;
  }
  private static final class UnionFind{private final int[] parent;UnionFind(int n){parent=new int[n];for(int i=0;i<n;i++)parent[i]=i;}int find(int x){return parent[x]==x?x:(parent[x]=find(parent[x]));}boolean union(int a,int b){a=find(a);b=find(b);if(a==b)return false;parent[a]=b;return true;}}
}
