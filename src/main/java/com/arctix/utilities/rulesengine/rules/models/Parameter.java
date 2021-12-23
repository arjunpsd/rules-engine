/* Use of this source code is subject to terms of MIT license.
 @author: Arjun Prasad
 @license: MIT
 @year: 2021 */
package com.arctix.utilities.rulesengine.rules.models;

import java.util.Objects;

public class Parameter {
  private ParameterKey key;
  private String dataValue;
  private String dataId;

  public Parameter() {}

  public Parameter(String compositeKey, String value) {
    key = new ParameterKey(compositeKey);
    dataValue = value;
  }

  public Parameter(ParameterKey parameterKey, String value) {
    key = parameterKey;
    dataValue = value;
  }

  public ParameterKey getKey() {
    return key;
  }

  public void setKey(ParameterKey key) {
    this.key = key;
  }

  public String getDataValue() {
    return dataValue;
  }

  public void setDataValue(String dataValue) {
    this.dataValue = dataValue;
  }

  public String getDataId() {
    return dataId;
  }

  public void setDataId(String dataId) {
    this.dataId = dataId;
  }

  @Override
  public String toString() {
    return "Parameter{"
        + "key="
        + key
        + ", dataValue='"
        + dataValue
        + '\''
        + ", dataId='"
        + dataId
        + '\''
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Parameter parameter = (Parameter) o;
    return key.equals(parameter.key)
        && Objects.equals(dataValue, parameter.dataValue)
        && Objects.equals(dataId, parameter.dataId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key);
  }
}
