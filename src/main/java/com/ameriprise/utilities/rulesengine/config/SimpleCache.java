/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine.config;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.CompletableFuture.runAsync;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/** Implements a simple In-memory cache using ConcurrentHashMap. */
public class SimpleCache {

  /*
   * Other potential implementations considered are:
   * Collections.synchronizedMap - better data consistency, but entire map is locked for read & write
   * LRUMap - implements max limits, but not thread-safe.
   */
  private final Map<String, CachedResource> cache = new ConcurrentHashMap<>();

  protected final int maxEntries;

  public SimpleCache() {
    this.maxEntries = 50;
  }

  public SimpleCache(int maxEntries) {
    this.maxEntries = maxEntries;
  }

  private AtomicLong hitCount = new AtomicLong();
  private AtomicLong fetchCount = new AtomicLong();

  public void add(String key, byte[] content, long expiry, long maxStale) {
    requireNonNull(content, "Content cannot be null");
    cache.put(key, new CachedResource(content, expiry, maxStale));
    purgeExcess();
  }

  public void add(String key, byte[] content, long expiry) {
    requireNonNull(content, "Content cannot be null");
    cache.put(key, new CachedResource(content, expiry, 300));
    purgeExcess();
  }

  public void add(String key, byte[] content) {
    add(key, content, 300);
  }

  public byte[] get(String key) {
    return get(key, false);
  }

  public byte[] get(String key, boolean acceptStale) {
    CachedResource resource = cache.get(key);
    fetchCount.incrementAndGet();

    if (isNull(resource)) {
      return null;
    }

    if (resource.hasExpired()) {
      purgeStale(); // purge expired & stale entries
      return acceptStale ? resource.content : null;
    }
    hitCount.incrementAndGet();
    return resource.content;
  }

  public void clear() {
    cache.clear();
  }

  public long count() {
    return cache.size();
  }

  public long getHitCount() {
    return hitCount.get();
  }

  public long getFetchCount() {
    return fetchCount.get();
  }

  private static final class CachedResource {
    long expiry;
    long maxStale;
    long cached;
    byte[] content;

    public CachedResource(byte[] content, long expiry, long maxStale) {
      this.expiry = expiry;
      this.content = content;
      this.cached = System.currentTimeMillis();
      this.maxStale = maxStale;
    }

    public boolean canPurge() {
      return (cached + (expiry * 1000) + (maxStale * 1000)) < System.currentTimeMillis();
    }

    public boolean hasExpired() {
      return (cached + (expiry * 1000)) < System.currentTimeMillis();
    }
  }

  private void purgeStale() {
    runAsync(
        () -> {
          Set<String> keysToDelete =
              cache.entrySet().stream()
                  .filter(entry -> entry.getValue().canPurge())
                  .map(Map.Entry::getKey)
                  .collect(Collectors.toSet());
          keysToDelete.forEach(cache::remove);
        });
  }

  private void purgeExcess() {
    final int deleteCount = cache.size() - maxEntries;
    if (deleteCount < 0) {
      return;
    }
    runAsync(
        () -> {
          Set<String> toDelete =
              cache.entrySet().stream()
                  .sorted((o1, o2) -> o1.getValue().cached < o2.getValue().cached ? -1 : 1)
                  .limit(deleteCount)
                  .map(Map.Entry::getKey)
                  .collect(Collectors.toSet());
          toDelete.forEach(cache::remove);
        });
  }
}
