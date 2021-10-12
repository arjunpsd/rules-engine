/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine.rules.models;

import java.util.List;

public class Feature {
  String name;
  Requirements requirements;
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

  @Override
  public String toString() {
    return "Feature {" + "name='" + name + '\'' + '}';
  }
}
