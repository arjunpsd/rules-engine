/* Use of this source code is subject to terms of MIT license.
 @author: Arjun Prasad
 @license: MIT
 @year: 2021 */
package com.arctix.utilities.rulesengine.rules.models;

import java.util.ArrayList;
import java.util.List;

public class Requirements {

  RequirementMatchingOptions options;
  List<EvaluationCondition> conditions;
  List<EvaluationCondition> preConditions;
  List<EvaluationCondition> postConditions;

  public Requirements() {
    conditions = new ArrayList<>();
    preConditions = new ArrayList<>();
    postConditions = new ArrayList<>();
  }

  public Requirements(
      List<EvaluationCondition> preConditions, List<EvaluationCondition> conditions) {
    this.conditions = conditions;
    this.preConditions = preConditions;
  }

  public List<EvaluationCondition> getConditions() {
    return conditions;
  }

  public void setConditions(List<EvaluationCondition> conditions) {
    this.conditions = conditions;
  }

  public RequirementMatchingOptions getOptions() {
    return options;
  }

  public void setOptions(RequirementMatchingOptions options) {
    this.options = options;
  }

  public List<EvaluationCondition> getPreConditions() {
    return preConditions;
  }

  public void setPreConditions(List<EvaluationCondition> preConditions) {
    this.preConditions = preConditions;
  }

  public List<EvaluationCondition> getPostConditions() {
    return postConditions;
  }

  public void setPostConditions(List<EvaluationCondition> postConditions) {
    this.postConditions = postConditions;
  }
}
