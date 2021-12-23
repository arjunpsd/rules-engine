/* Use of this source code is subject to terms of MIT license.
 @author: Arjun Prasad
 @license: MIT
 @year: 2021 */
package com.arctix.utilities.rulesengine.config;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RemoteConfigProviderTest {

  final String configUrl = "https://httpbin.org/anything";

  @Test
  public void testFetchConfig() throws Exception {
    // given
    RemoteConfigProvider remoteConfigProvider =
        new RemoteConfigProvider(
            new RemoteConfigProperties.Builder()
                .basicAuth(Credentials.basic("username", "password"))
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
    Response result = remoteConfigProvider.getConfig(configUrl, Response.class);

    // then
    assertNotNull(result.url);
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
    remoteConfigProvider.getConfig("https://httpbin.org/status/500");

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

  private static final class Response {
    String url;
    String method;
    String origin;
  }
}
