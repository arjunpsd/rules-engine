/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine.rules;

import static java.util.Objects.requireNonNull;

import java.io.InputStreamReader;

import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.ameriprise.utilities.rulesengine.rules.models.Parameter;
import com.ameriprise.utilities.rulesengine.rules.models.ParameterJsonDeserializer;
import com.ameriprise.utilities.rulesengine.rules.models.Rules;

@Component
public class ClassPathRulesLoader implements RulesLoader {

  @Override
  public Rules load(String ruleSetName) {
    String fileName = "/" + ruleSetName + ".json";

    GsonBuilder builder = new GsonBuilder();
    Gson gson =
        builder.registerTypeAdapter(Parameter.class, new ParameterJsonDeserializer()).create();
    return gson.fromJson(
        new InputStreamReader(requireNonNull(this.getClass().getResourceAsStream(fileName))),
        Rules.class);
  }
}
