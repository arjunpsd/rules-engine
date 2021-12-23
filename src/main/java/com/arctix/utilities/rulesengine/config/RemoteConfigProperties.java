/* Use of this source code is subject to terms of MIT license.
 @author: Arjun Prasad
 @license: MIT
 @year: 2021 */
package com.arctix.utilities.rulesengine.config;

public class RemoteConfigProperties {

  private String basicAuth;

  private boolean useCache;

  private int cacheExpiry = 300;

  private int maxStale = 300;

  private RemoteConfigProperties(Builder builder) {
    this.basicAuth = builder.basicAuth;
    this.useCache = builder.useCache;
    this.cacheExpiry = builder.cacheExpiry;
    this.maxStale = builder.maxStale;
  }

  public String getBasicAuth() {
    return basicAuth;
  }

  public boolean shouldUseCache() {
    return useCache;
  }

  public int getCacheExpiry() {
    return cacheExpiry;
  }

  public int getMaxStale() {
    return maxStale;
  }

  public static final class Builder {
    String basicAuth;
    boolean useCache = false;
    int cacheExpiry = 300;
    int maxStale = 300;

    public Builder basicAuth(String basicAuth) {
      this.basicAuth = basicAuth;
      return this;
    }

    public Builder useCache(boolean useCache) {
      this.useCache = useCache;
      return this;
    }

    public Builder cacheExpiry(int cacheExpiry) {
      this.cacheExpiry = cacheExpiry;
      return this;
    }

    public Builder maxStale(int maxStale) {
      this.maxStale = maxStale;
      return this;
    }

    public RemoteConfigProperties build() {
      return new RemoteConfigProperties(this);
    }
  }
}
