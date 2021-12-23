/* Use of this source code is subject to terms of MIT license.
 @author: Arjun Prasad
 @license: MIT
 @year: 2021 */
package com.arctix.utilities.rulesengine.rules.models;

import com.google.gson.*;
import java.lang.reflect.Type;

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
