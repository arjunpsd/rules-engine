/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine.rules.models;

import java.util.Objects;

public class ParameterKey {
  private String dataSource;
  private String name;

  public ParameterKey() {}

  public ParameterKey(String compositeKey) {
    String[] paramKeys = compositeKey.split(":");
    dataSource = paramKeys[0];
    name = paramKeys[1];
  }

  public ParameterKey(String dataSource, String name) {
    this.dataSource = dataSource;
    this.name = name;
  }

  public String getDataSource() {
    return dataSource;
  }

  public void setDataSource(String dataSource) {
    this.dataSource = dataSource;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ParameterKey that = (ParameterKey) o;
    return dataSource.equals(that.dataSource) && name.equals(that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dataSource, name);
  }

  @Override
  public String toString() {
    return dataSource + ":" + name;
  }
}
