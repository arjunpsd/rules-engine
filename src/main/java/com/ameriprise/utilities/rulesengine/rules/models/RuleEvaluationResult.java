/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine.rules.models;

import java.util.List;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

public class RuleEvaluationResult {
  String feature;
  String returnValue;
  List<EvaluatedParameter> matched;

  public RuleEvaluationResult() {}

  public RuleEvaluationResult(
      String feature, List<EvaluatedParameter> matched, String returnValue) {
    this.feature = feature;
    this.matched = matched;
    this.returnValue = returnValue;
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

  public String getReturnValue() {
    return returnValue;
  }

  public void setReturnValue(String returnValue) {
    this.returnValue = returnValue;
  }

  public boolean hasMatch() {
    return isNotEmpty(this.matched);
  }

  @Override
  public String toString() {
    return "RuleEvaluationResult {"
        + "feature='"
        + feature
        + '\''
        + ", returnValue='"
        + returnValue
        + '\''
        + ", matched="
        + matched
        + '}';
  }
}
