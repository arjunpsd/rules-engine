/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine.rules;

import static java.util.Objects.*;

import java.io.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.ameriprise.utilities.rulesengine.config.RemoteConfigProvider;
import com.ameriprise.utilities.rulesengine.rules.models.Parameter;
import com.ameriprise.utilities.rulesengine.rules.models.ParameterJsonDeserializer;
import com.ameriprise.utilities.rulesengine.rules.models.Rules;

@Component
public class RulesFileLoader implements RulesLoader {

  private static final String HTTP = "http";
  private static final String CLASSPATH = "classpath:";
  private static final String FILE = "file:";

  private RemoteConfigProvider config;

  @Autowired(required = false)
  public RulesFileLoader(RemoteConfigProvider config) {
    this.config = config;
  }

  public RulesFileLoader() {}

  @Override
  public Rules load(String path) {
    requireNonNull(path);
    Rules rules;
    try {
      if (path.startsWith(HTTP)) {
        rules = loadFromRemoteLocation(path);
      } else if (path.startsWith(CLASSPATH)) {
        String fileName = path.substring(10);
        rules = loadFromClasspath(fileName);
      } else if (path.startsWith(FILE)) {
        String filName = path.substring(5);
        rules = loadFromFileSystem(filName);
      } else if (!path.endsWith(".json")) {
        String filName = "/" + path + ".json";
        rules = loadFromClasspath(filName);
      } else {
        throw new RuntimeException("Unable to load file from location: " + path);
      }
    } catch (IOException ioException) {
      throw new RuntimeException(ioException);
    }
    return rules;
  }

  @Override
  public Rules load(InputStream inputStream) {
    GsonBuilder builder = new GsonBuilder();
    Gson gson =
        builder.registerTypeAdapter(Parameter.class, new ParameterJsonDeserializer()).create();
    return gson.fromJson(new InputStreamReader(inputStream), Rules.class);
  }

  @Override
  public Rules load(byte[] content) {
    requireNonNull(content);
    return load(new ByteArrayInputStream(content));
  }

  @Override
  public void cleanUp() {
    if (nonNull(config)) {
      config.clearCache();
    }
  }

  private Rules loadFromClasspath(String fileName) throws IOException {
    try (InputStream inputStream = this.getClass().getResourceAsStream(fileName)) {
      if (isNull(inputStream)) {
        throw new IOException("File not found " + fileName);
      }
      return load(inputStream);
    }
  }

  private Rules loadFromFileSystem(String filName) throws IOException {
    try (InputStream inputStream = new FileInputStream(filName)) {
      return load(inputStream);
    }
  }

  private Rules loadFromRemoteLocation(String url) throws IOException {
    requireNonNull(config, "Remote Config Provider not initialized");
    return load(config.getConfig(url));
  }
}
