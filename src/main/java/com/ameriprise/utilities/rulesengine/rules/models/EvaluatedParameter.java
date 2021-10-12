/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine.rules.models;

import java.util.List;

public class EvaluatedParameter {
  EvaluationCondition condition;
  List<Parameter> parameters;

  public EvaluatedParameter(EvaluationCondition condition, List<Parameter> parameters) {
    this.condition = condition;
    this.parameters = parameters;
  }

  public EvaluationCondition getCondition() {
    return condition;
  }

  public void setCondition(EvaluationCondition condition) {
    this.condition = condition;
  }

  public List<Parameter> getParameters() {
    return parameters;
  }

  public void setParameters(List<Parameter> parameters) {
    this.parameters = parameters;
  }

  @Override
  public String toString() {
    return "Evaluation { Condition = " + condition + ", Parameters = " + parameters + "}";
  }
}
