/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine.validation;

import com.ameriprise.utilities.rulesengine.RulesEngine;
import com.ameriprise.utilities.rulesengine.rules.RulesLoader;
import com.ameriprise.utilities.rulesengine.rules.models.Feature;
import com.ameriprise.utilities.rulesengine.rules.models.RuleEvaluationResult;
import com.ameriprise.utilities.rulesengine.rules.models.Rules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

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
            .filter(ruleEvaluationResult -> !ruleEvaluationResult.hasMatch())
            .collect(Collectors.toList());

    // check for failures
    testResults.stream()
        .forEach(result -> LOG.warn("Expected matching results for rules {}", testResults));

    if (isNotEmpty(testResults)) {
      throw new RuntimeException("Business rule tests failed!");
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
}
