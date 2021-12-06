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

/**
 * A test runner that takes in a set of business rules, corresponding tests and validates the
 * business rules using those tests. The test cases themselves are written using business rules DSL
 * and therefore can be executed using the RulesEngine
 */
@Component
public class RulesValidator {

  @Autowired RulesLoader rulesLoader;

  @Autowired RulesEngine rulesEngine;

  private static final Logger LOG = LoggerFactory.getLogger(RulesValidator.class);

  /**
   * Validates the rules given in the business rules file json. Test rules should have the same name
   * as the business rules file, but with a suffix of `-test`
   *
   * @param rulesLocation
   */
  public void validateRules(String rulesLocation) throws RulesValidationException {
    validateRules(rulesLocation, rulesLocation + "-tests");
  }

  /**
   * Validates the rules given in the business rules file json.
   *
   * @param rulesLocation
   */
  public void validateRules(String rulesLocation, String testRules)
      throws RulesValidationException {
    final Rules rules = rulesLoader.load(rulesLocation);
    final Rules tests = rulesLoader.load(testRules);
    try {
      validateRules(rules, tests);
    } catch (RulesValidationException exception) {
      // prevent rules loader from caching results if rules are bad
      rulesLoader.cleanUp();
      throw exception;
    }
  }

  /**
   * Runs the tests against the given rules. Throws runtime exception if tests fail.
   *
   * @param rules - rules to be validated
   * @param tests - test cases for validation
   */
  public void validateRules(Rules rules, Rules tests) throws RulesValidationException {

    // execute the test using rules engine
    List<RuleEvaluationResult> testResults =
        tests.getFeatures().stream()
            .map(test -> validateRules(test, rules))
            .flatMap(listCompletableFuture -> listCompletableFuture.join().stream())
            .collect(Collectors.toList());

    assertTestsHavePassed(testResults);

    // assertTestsHaveCoverage(rules, testResults);
  }

  /**
   * Execute the test using the Rules Engine
   *
   * @param test
   * @param rulesToValidate
   * @return
   */
  private CompletableFuture<List<RuleEvaluationResult>> validateRules(
      Feature test, Rules rulesToValidate) {

    // rules to be tested are passed as userData
    Map<String, Rules> userData = new HashMap<>();
    userData.put("rules", rulesToValidate);

    Rules tests = new Rules(Collections.singletonList(test));
    userData.put("tests", tests);

    LOG.info("Executing Test: {}", test.getName());
    return rulesEngine.executeRules(tests, userData);
  }

  /**
   * Asserts that there are no failed tests.
   *
   * @param testResults
   */
  private void assertTestsHavePassed(List<RuleEvaluationResult> testResults)
      throws RulesValidationException {
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
      throw new RulesValidationException("Business rule tests failed!");
    }
  }

  /**
   * Asserts that there is at least one positive and negative test per feature in the business rule
   *
   * @param rules
   * @param testResults
   */
  private void assertTestsHaveCoverage(Rules rules, List<RuleEvaluationResult> testResults)
      throws RulesValidationException {

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
      throw new RulesValidationException("Coverage not adequate. Missing positive tests!");
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
      throw new RulesValidationException("Coverage not adequate. Missing negative tests!");
    }
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
