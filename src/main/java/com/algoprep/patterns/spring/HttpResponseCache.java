package com.algoprep.patterns.spring;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

/** Bounded LRU response cache whose entries expire after their TTL. */
public class HttpResponseCache {
  private final Map<String,Entry> entries;
  public HttpResponseCache(int capacity){if(capacity<=0)throw new IllegalArgumentException("capacity must be positive");entries=new LinkedHashMap<>(capacity,0.75f,true){protected boolean removeEldestEntry(Map.Entry<String,Entry> eldest){return size()>capacity;}};}
  public synchronized String get(String key){Entry entry=entries.get(key);if(entry==null)return null;if(System.nanoTime()>=entry.expiresAt){entries.remove(key);return null;}return entry.body;}
  public synchronized void put(String key,String body,Duration ttl){if(ttl.isNegative())throw new IllegalArgumentException("ttl cannot be negative");entries.put(key,new Entry(body,System.nanoTime()+ttl.toNanos()));}
  private record Entry(String body,long expiresAt) {}
}
