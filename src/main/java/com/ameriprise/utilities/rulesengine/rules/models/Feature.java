/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine.rules.models;

import java.util.List;
import java.util.Objects;

public class Feature {
  String name;
  Requirements requirements;
  List<Action> actions;
  List<Parameter> testData;

  public Feature() {}

  public Feature(String name, Requirements requirements) {
    this.name = name;
    this.requirements = requirements;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Requirements getRequirements() {
    return requirements;
  }

  public void setRequirements(Requirements requirements) {
    this.requirements = requirements;
  }

  public List<Parameter> getTestData() {
    return testData;
  }

  public void setTestData(List<Parameter> testData) {
    this.testData = testData;
  }

  public List<Action> getActions() {
    return actions;
  }

  public void setActions(List<Action> actions) {
    this.actions = actions;
  }

  @Override
  public String toString() {
    return "Feature {" + "name='" + name + '\'' + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Feature feature = (Feature) o;
    return name.equals(feature.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
