/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine.config;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SimpleCacheTest {

  byte[] sampleContent = "this is some content".getBytes();

  @Test
  public void testAddAndGet() throws Exception {
    // given
    SimpleCache cache = new SimpleCache();
    cache.add("k1", sampleContent);
    Thread.sleep(500);

    // when
    byte[] result = cache.get("k1");

    // then
    assertNotNull(result);
    assertEquals(1, cache.count());
    assertEquals(1, cache.getHitCount());
    assertEquals(1, cache.getFetchCount());
  }

  @Test
  public void testAddAndGetExpired() throws Exception {

    // given
    SimpleCache cache = new SimpleCache();
    cache.add("k1", sampleContent, 0);
    cache.add("k2", sampleContent, 10);
    Thread.sleep(500);

    // when
    byte[] result = cache.get("k1"); // expired hit
    assertNull(result);

    result = cache.get("k2"); // valid hit
    assertNotNull(result);

    // then
    assertEquals(2, cache.count());
    assertEquals(1, cache.getHitCount());
    assertEquals(2, cache.getFetchCount());
  }

  @Test
  public void testAddAndGetStale() throws Exception {

    // given
    SimpleCache cache = new SimpleCache();
    cache.add("k1", sampleContent, 0, 10);
    Thread.sleep(500);

    // when
    byte[] result = cache.get("k1", false); // expired hit
    assertNull(result);
    result = cache.get("k1", true); // stale hit
    assertNotNull(result);

    // then
    assertEquals(1, cache.count());
    assertEquals(0, cache.getHitCount());
    assertEquals(2, cache.getFetchCount());
  }

  @Test
  public void testAddAndPurge() throws Exception {

    // given
    SimpleCache cache = new SimpleCache();
    for (int i = 0; i < cache.maxEntries + 50; i++) {
      cache.add("k" + i, sampleContent);
      Thread.sleep(5);
    }
    Thread.sleep(500);

    // then
    assertEquals(cache.maxEntries, cache.count());
    assertNotNull(cache.get("k" + (cache.maxEntries))); // earliest
    assertNotNull(cache.get("k" + (cache.maxEntries + 49))); // latest
  }
}
