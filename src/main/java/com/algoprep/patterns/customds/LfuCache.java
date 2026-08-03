package com.algoprep.patterns.customds;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/** O(1) least-frequently-used cache with LRU tie-breaking. */
public class LfuCache {
  private final int capacity; private int minFrequency;
  private final Map<Integer,Node> nodes=new HashMap<>(); private final Map<Integer,LinkedHashSet<Integer>> keysByFrequency=new HashMap<>();
  public LfuCache(int capacity){this.capacity=capacity;}
  public int get(int key){Node node=nodes.get(key);if(node==null)return -1;touch(node);return node.value;}
  public void put(int key,int value){if(capacity==0)return;Node existing=nodes.get(key);if(existing!=null){existing.value=value;touch(existing);return;}if(nodes.size()==capacity){int evict=keysByFrequency.get(minFrequency).iterator().next();keysByFrequency.get(minFrequency).remove(evict);nodes.remove(evict);}Node node=new Node(key,value);nodes.put(key,node);keysByFrequency.computeIfAbsent(1,ignored->new LinkedHashSet<>()).add(key);minFrequency=1;}
  private void touch(Node node){LinkedHashSet<Integer> old=keysByFrequency.get(node.frequency);old.remove(node.key);if(node.frequency==minFrequency&&old.isEmpty())minFrequency++;node.frequency++;keysByFrequency.computeIfAbsent(node.frequency,ignored->new LinkedHashSet<>()).add(node.key);}
  private static final class Node{private final int key;private int value,frequency=1;Node(int key,int value){this.key=key;this.value=value;}}
}
