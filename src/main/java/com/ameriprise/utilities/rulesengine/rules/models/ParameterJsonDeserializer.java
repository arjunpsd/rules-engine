/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine.rules.models;

import java.lang.reflect.Type;

import com.google.gson.*;

public class ParameterJsonDeserializer implements JsonDeserializer<Parameter> {

  @Override
  public Parameter deserialize(
      JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
      throws JsonParseException {
    JsonObject jsonObject = jsonElement.getAsJsonObject();
    Parameter parameter = new Parameter();
    parameter.setKey(new ParameterKey(jsonObject.get("key").getAsString()));
    JsonElement dataValue = jsonObject.get("dataValue");
    if (!dataValue.isJsonNull()) {
      parameter.setDataValue(dataValue.getAsString());
    }
    JsonElement dataId = jsonObject.get("dataId");
    if (!dataId.isJsonNull()) {
      parameter.setDataId(dataId.getAsString());
    }
    return parameter;
  }
}
