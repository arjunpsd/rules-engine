/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine.rules.models;

import java.util.List;

public class RuleEvaluationResult {
  String feature;
  List<EvaluatedParameter> matched;
  List<EvaluatedParameter> evaluated;

  public RuleEvaluationResult() {}

  public RuleEvaluationResult(String feature, List<EvaluatedParameter> matched) {
    this.feature = feature;
    this.matched = matched;
  }

  public String getFeature() {
    return feature;
  }

  public void setFeature(String feature) {
    this.feature = feature;
  }

  public List<EvaluatedParameter> getMatched() {
    return matched;
  }

  public void setMatched(List<EvaluatedParameter> matched) {
    this.matched = matched;
  }

  public List<EvaluatedParameter> getEvaluated() {
    return evaluated;
  }

  public void setEvaluated(List<EvaluatedParameter> evaluated) {
    this.evaluated = evaluated;
  }

  @Override
  public String toString() {
    return "RuleEvaluationResult{"
        + "feature='"
        + feature
        + '\''
        + ", matched="
        + matched
        + ", evaluated="
        + evaluated
        + '}';
  }
}
