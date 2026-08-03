package com.algoprep.patterns.unionfind;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Merges accounts sharing at least one email address. */
public final class AccountsMerge {
  private AccountsMerge() {}
  public static List<List<String>> accountsMerge(List<List<String>> accounts) {
    UnionFind uf = new UnionFind(accounts.size()); Map<String,Integer> owner=new HashMap<>();
    for(int i=0;i<accounts.size();i++) for(int j=1;j<accounts.get(i).size();j++){String email=accounts.get(i).get(j);Integer other=owner.putIfAbsent(email,i);if(other!=null)uf.union(i,other);}
    Map<Integer,List<String>> emails=new HashMap<>(); for(Map.Entry<String,Integer> e:owner.entrySet()) emails.computeIfAbsent(uf.find(e.getValue()), ignored->new ArrayList<>()).add(e.getKey());
    List<List<String>> result=new ArrayList<>(); for(Map.Entry<Integer,List<String>> e:emails.entrySet()){Collections.sort(e.getValue());List<String> account=new ArrayList<>();account.add(accounts.get(e.getKey()).get(0));account.addAll(e.getValue());result.add(account);} return result;
  }
  private static final class UnionFind { private final int[] parent,rank; UnionFind(int n){parent=new int[n];rank=new int[n];for(int i=0;i<n;i++)parent[i]=i;} int find(int x){return parent[x]==x?x:(parent[x]=find(parent[x]));} void union(int a,int b){a=find(a);b=find(b);if(a==b)return;if(rank[a]<rank[b])parent[a]=b;else{parent[b]=a;if(rank[a]==rank[b])rank[a]++;}} }
}
