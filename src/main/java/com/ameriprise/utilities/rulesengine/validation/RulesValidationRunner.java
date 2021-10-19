/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine.validation;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ameriprise.utilities.rulesengine.RulesEngine;
import com.ameriprise.utilities.rulesengine.rules.RulesLoader;
import com.ameriprise.utilities.rulesengine.rules.models.*;

@Component
public class RulesValidationRunner {

  @Autowired RulesLoader rulesLoader;

  @Autowired RulesEngine rulesEngine;

  private static final Logger LOG = LoggerFactory.getLogger(RulesValidationRunner.class);

  public void validateRules(String ruleSetName) {
    final Rules rules = rulesLoader.load(ruleSetName);
    final Rules tests = rulesLoader.load(ruleSetName + "-tests");

    List<RuleEvaluationResult> testResults =
        tests.getFeatures().stream()
            .map(test -> validateRules(test, rules))
            .flatMap(listCompletableFuture -> listCompletableFuture.join().stream())
            .collect(Collectors.toList());

    // check for failures
    List<RuleEvaluationResult> failedTests =
        testResults.stream()
            .filter(ruleEvaluationResult -> !ruleEvaluationResult.hasMatch())
            .collect(Collectors.toList());
    if (isNotEmpty(failedTests)) {
      failedTests.forEach(
          result ->
              LOG.error(
                  "Expected matching results for feature {}, but found none.",
                  result.getFeature()));
      throw new RuntimeException("Business rule tests failed!");
    }

    // check for coverage
    List<String> positiveMatches =
        getMatchedFeatures(testResults).stream()
            .map(EvaluationCondition::getEquals)
            .collect(Collectors.toList());
    List<String> missingPositiveTests =
        rules.getFeatures().stream()
            .map(Feature::getName)
            .filter(featureName -> !positiveMatches.contains(featureName))
            .collect(Collectors.toList());

    if (isNotEmpty(missingPositiveTests)) {
      LOG.error("Missing positive tests for features: {}", missingPositiveTests);
      throw new RuntimeException("Coverage not adequate. Missing positive tests!");
    }

    List<String> negativeMatches =
        getMatchedFeatures(testResults).stream()
            .map(EvaluationCondition::getNotEquals)
            .collect(Collectors.toList());

    List<String> missingNegativeTests =
        rules.getFeatures().stream()
            .map(Feature::getName)
            .filter(featureName -> !negativeMatches.contains(featureName))
            .collect(Collectors.toList());

    if (isNotEmpty(missingNegativeTests)) {
      LOG.error("Missing negative tests for features: {}", missingNegativeTests);
      throw new RuntimeException("Coverage not adequate. Missing negative tests!");
    }
  }

  private CompletableFuture<List<RuleEvaluationResult>> validateRules(
      Feature test, Rules rulesToValidate) {
    Rules tests = new Rules(Collections.singletonList(test));
    Map<String, Rules> userData = new HashMap<>();
    userData.put("rules", rulesToValidate);
    userData.put("tests", tests);
    LOG.debug("Executing Test: {}", test.getName());
    return rulesEngine.executeRules(tests, userData);
  }

  private List<EvaluationCondition> getMatchedFeatures(List<RuleEvaluationResult> testResults) {
    return testResults.stream()
        .flatMap(
            result ->
                result.getMatched().stream()
                    .map(EvaluatedParameter::getCondition)
                    .filter(
                        condition ->
                            condition
                                .getKey()
                                .equalsIgnoreCase(
                                    "rules-validator:rule-execution-result.feature-name")))
        .collect(Collectors.toList());
  }
}
