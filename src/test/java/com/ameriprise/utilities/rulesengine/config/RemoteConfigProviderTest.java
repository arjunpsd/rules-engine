/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine.config;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RemoteConfigProviderTest {

  final String configUrl =
      "https://cis-01.qa.ampf.com/config/utilities-api-gateway/dev/develop/validatingJwks.json";

  @Test
  public void testFetchConfig() throws Exception {
    // given
    RemoteConfigProvider remoteConfigProvider =
        new RemoteConfigProvider(
            new RemoteConfigProperties.Builder()
                .basicAuth(Credentials.basic("root", "root"))
                .build());

    // when
    byte[] result = remoteConfigProvider.getConfig(configUrl);

    // then
    assertNotNull(result);
    assertEquals(0, remoteConfigProvider.cache.count());
  }

  @Test
  public void testFetchConfigWithCache() throws Exception {
    // given
    RemoteConfigProvider remoteConfigProvider =
        new RemoteConfigProvider(
            new RemoteConfigProperties.Builder()
                .useCache(true)
                .basicAuth(Credentials.basic("root", "root"))
                .build());

    // when
    remoteConfigProvider.getConfig(configUrl);
    Keys result = remoteConfigProvider.getConfig(configUrl, Keys.class);

    // then
    assertNotNull(result);
    assertTrue(result.keys.size() > 0);
    assertEquals(1, remoteConfigProvider.cache.count());
    assertEquals(2, remoteConfigProvider.cache.getFetchCount());
    assertEquals(1, remoteConfigProvider.cache.getHitCount());
  }

  @Test(expected = IOException.class)
  public void testFetchConfigBadUrl() throws Exception {
    // given
    RemoteConfigProvider remoteConfigProvider =
        new RemoteConfigProvider(new RemoteConfigProperties.Builder().build());

    // when
    remoteConfigProvider.getConfig("https://cis-01.qa.ampf.com/test-404");

    // then
    fail("should have thrown exception");
  }

  @Test
  public void testFetchConfigWithFallback() throws Exception {
    // given
    RemoteConfigProvider remoteConfigProvider =
        new RemoteConfigProvider(
            new RemoteConfigProperties.Builder()
                .useCache(true)
                .basicAuth(Credentials.basic("root", "root"))
                .cacheExpiry(0)
                .build());
    remoteConfigProvider.getConfig(configUrl); // call to cache result first time

    // when
    Thread.sleep(500);
    remoteConfigProvider.client = mockHttpClient();
    byte[] result = remoteConfigProvider.getConfig(configUrl); // second request for config

    // then
    assertNotNull(result);
    assertEquals(1, remoteConfigProvider.cache.count());
    assertEquals(3, remoteConfigProvider.cache.getFetchCount());
    assertEquals(0, remoteConfigProvider.cache.getHitCount());
  }

  private OkHttpClient mockHttpClient() throws IOException {
    OkHttpClient client = mock(OkHttpClient.class);
    Call call = mock(Call.class);
    when(call.execute()).thenThrow(new IOException("Failed to connect to resource"));
    when(client.newCall(any())).thenReturn(call);
    return client;
  }

  private static final class Keys {
    List<Key> keys;

    public List<Key> getKeys() {
      return keys;
    }

    public void setKeys(List<Key> keys) {
      this.keys = keys;
    }
  }

  private static final class Key {
    String kid;

    public String getKid() {
      return kid;
    }

    public void setKid(String kid) {
      this.kid = kid;
    }
  }
}
