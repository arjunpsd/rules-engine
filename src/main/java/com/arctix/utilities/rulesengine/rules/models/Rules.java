/* Use of this source code is subject to terms of MIT license.
 @author: Arjun Prasad
 @license: MIT
 @year: 2021 */
package com.arctix.utilities.rulesengine.rules.models;

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
