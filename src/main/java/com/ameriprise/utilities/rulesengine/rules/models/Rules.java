/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine.rules.models;

import java.util.List;

public class Rules {
  List<Feature> features;

  public Rules(List<Feature> features) {
    this.features = features;
  }

  public Rules() {}

  public List<Feature> getFeatures() {
    return features;
  }

  public void setFeatures(List<Feature> features) {
    this.features = features;
  }
}
