/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine.rules;

import static com.ameriprise.utilities.rulesengine.rules.Matchers.*;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ameriprise.utilities.rulesengine.datasources.models.DataSet;
import com.ameriprise.utilities.rulesengine.rules.models.*;

public class RulesEvaluator {

  private Rules rules;
  private DataSet dataSet;
  private Options options = Options.CONDITIONS_ONLY;

  private static final Logger LOG = LoggerFactory.getLogger(RulesEvaluator.class);

  public RulesEvaluator(DataSet dataSet, Rules rules, Options options) {
    this.dataSet = dataSet;
    this.rules = rules;
    this.options = options;
  }

  public RulesEvaluator(DataSet dataSet, Rules rules) {
    this.dataSet = dataSet;
    this.rules = rules;
  }

  public List<RuleEvaluationResult> evaluate() {
    requireNonNull(rules, "Rules cannot be null");
    requireNonNull(dataSet, "data set cannot be null");
    return rules.getFeatures().stream()
        .filter(this::evaluateFeature)
        .map(feature -> new RuleEvaluationResult(feature.getName(), toMatchResult(feature)))
        .collect(Collectors.toList());
  }

  private boolean evaluateFeature(Feature feature) {
    // TODO: assume all conditions must match. ie requirement.options.combination = AND
    return getConditionsToEvaluate(feature.getRequirements()).stream()
        .allMatch(this::evaluateCondition);
  }

  private List<EvaluationCondition> getConditionsToEvaluate(Requirements requirements) {
    switch (options) {
      case PRE_CONDITIONS_ONLY:
        return requirements.getPreConditions();
      case POST_CONDITIONS_ONLY:
        return requirements.getPostConditions();
      default:
        return requirements.getConditions();
    }
  }

  protected boolean evaluateCondition(EvaluationCondition condition) {
    List<Parameter> parameterValues = dataSet.getParameters(condition.getKey());
    boolean evalResult;
    if (isEmpty(parameterValues)) {
      LOG.debug(
          "No data exists for evaluating condition {}. Evaluating with null value.", condition);
      // If data value does not exist, assume null value
      evalResult = isMatch(condition, new Parameter(condition.getKey(), null));
    } else if (nonNull(condition.getNotEquals())
        || nonNull(condition.getNotOneOf())
        || nonNull(condition.getNotContains())) {
      // do ALL match for negative conditions
      evalResult = parameterValues.stream().allMatch(parameter -> isMatch(condition, parameter));
    } else {
      // do ANY match for positive conditions
      evalResult = parameterValues.stream().anyMatch(parameter -> isMatch(condition, parameter));
    }

    LOG.debug(
        "Evaluation of condition {} with data {} resulted in {}",
        condition,
        parameterValues,
        evalResult);
    return evalResult;
  }

  protected boolean isMatch(EvaluationCondition condition, Parameter parameter) {
    if (nonNull(condition.getEquals())) {
      return isEqualTo(condition.getEquals(), parameter);
    } else if (nonNull(condition.getNotEquals())) {
      return isNotEqualTo(condition.getNotEquals(), parameter);
    } else if (nonNull(condition.getOneOf())) {
      return isOneOf(condition.getOneOf(), parameter);
    } else if (nonNull(condition.getNotOneOf())) {
      return isNotOneOf(condition.getNotOneOf(), parameter);
    } else if (nonNull(condition.getContains())) {
      return contains(condition.getContains(), parameter);
    } else if (nonNull(condition.getNotContains())) {
      return notContains(condition.getNotContains(), parameter);
    } else if (condition.getWithinDays() > 0) {
      return isWithinDays(condition.getWithinDays(), parameter);
    } else if (condition.getBeyondDays() > 0) {
      return isBeyondDays(condition.getBeyondDays(), parameter);
    }
    LOG.warn("Condition not implemented: {}. Assuming negative evaluation.", condition);
    return false;
  }

  protected List<EvaluatedParameter> toMatchResult(Feature feature) {
    return feature.getRequirements().getConditions().stream()
        .map(condition -> new EvaluatedParameter(condition, getMatchedParameter(condition)))
        .collect(Collectors.toList());
  }

  protected List<Parameter> getMatchedParameter(EvaluationCondition condition) {
    if (nonNull(condition.getNotEquals())
        || nonNull(condition.getNotOneOf())
        || nonNull(condition.getNotContains())) {
      dataSet.getParameters(condition.getKey()).stream()
          .filter(parameter -> !isMatch(condition, parameter))
          .collect(Collectors.toList());
    }
    return dataSet.getParameters(condition.getKey()).stream()
        .filter(parameter -> isMatch(condition, parameter))
        .collect(Collectors.toList());
  }

  public enum Options {
    PRE_CONDITIONS_ONLY,
    CONDITIONS_ONLY,
    POST_CONDITIONS_ONLY
  }
}
