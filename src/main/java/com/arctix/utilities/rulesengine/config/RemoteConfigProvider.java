/* Use of this source code is subject to terms of MIT license.
 @author: Arjun Prasad
 @license: MIT
 @year: 2021 */
package com.arctix.utilities.rulesengine.config;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RemoteConfigProvider {

  protected final SimpleCache cache = new SimpleCache(10);

  protected OkHttpClient client;

  private static final Logger LOG = LoggerFactory.getLogger(RemoteConfigProvider.class);

  private RemoteConfigProperties config;

  @Autowired(required = false)
  public RemoteConfigProvider(RemoteConfigProperties config) {
    this.config = config;
    initHttpClient();
  }

  public RemoteConfigProvider() {
    this(new RemoteConfigProperties.Builder().build());
  }

  public void clearCache() {
    cache.clear();
  }

  public byte[] getConfig(String resourceUrl) throws IOException {
    if (config.shouldUseCache()) {
      byte[] content = cache.get(resourceUrl);
      if (isNull(content)) {
        content = fetchRemoteConfig(resourceUrl);
        if (nonNull(content)) {
          cache.add(resourceUrl, content, config.getCacheExpiry(), config.getMaxStale());
        }
      }
      return content;
    } else {
      return fetchRemoteConfig(resourceUrl);
    }
  }

  public <T> T getConfig(String resourceUrl, Class<T> clazz) throws IOException {
    byte[] content = getConfig(resourceUrl);
    if (isNull(content)) {
      return null;
    }
    return new Gson().fromJson(new String(content), clazz);
  }

  protected byte[] fetchRemoteConfig(String resourceUrl) throws IOException {
    Request request = new Request.Builder().url(resourceUrl).build();
    byte[] content;
    try (Response response = client.newCall(request).execute()) {
      if (response.isSuccessful()) {
        LOG.info("Successfully fetched config from url {}", request.url());
        content = response.body().bytes();
      } else {
        throw new IOException(
            "Server returned status " + response.code() + " when fetching remote config");
      }
    } catch (IOException ioException) {
      content = cache.get(resourceUrl, true);
      if (isNull(content)) {
        LOG.error(
            "Failed to fetch config from url {}. Not available in cache either.",
            resourceUrl,
            ioException);
        throw ioException;
      }
      LOG.warn("Falling back to cached response for url {}.", resourceUrl);
    }
    return content;
  }

  protected void initHttpClient() {
    // TODO: enable HTTP Cache with given config
    client =
        new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .callTimeout(15, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .authenticator(
                (route, response) -> {
                  if (isBlank(config.getBasicAuth())
                      || response.request().header("Authorization") != null) {
                    return null;
                  }
                  return response
                      .request()
                      .newBuilder()
                      .header("Authorization", config.getBasicAuth())
                      .build();
                })
            .build();
  }
}
