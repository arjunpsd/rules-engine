/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ameriprise.utilities.rulesengine.datasources.DataFetchService;
import com.ameriprise.utilities.rulesengine.datasources.models.DataSet;
import com.ameriprise.utilities.rulesengine.rules.RulesEvaluator;
import com.ameriprise.utilities.rulesengine.rules.RulesLoader;
import com.ameriprise.utilities.rulesengine.rules.models.*;

/** Entry Point for executing the Rules Engine. */
@Component
public class RulesEngine {

  private DataFetchService dataService;

  private RulesLoader rulesLoader;

  private static final Logger LOG = LoggerFactory.getLogger(RulesEngine.class);

  @Autowired
  public RulesEngine(DataFetchService dataService, RulesLoader rulesLoader) {
    this.dataService = dataService;
    this.rulesLoader = rulesLoader;
  }

  /**
   * Loads & evaluates rules from the given file.
   *
   * @param ruleSetName - name of the file containing the rules to be evaluated
   * @param userData - contextual data that is passed to `DataAdaptor`s, which are plugin components
   *     (spring beans) used to fetch data for evaluating rules.
   * @return
   */
  public CompletableFuture<List<RuleEvaluationResult>> executeRules(
      String ruleSetName, Map<?, ?> userData) {
    final Rules rules = rulesLoader.load(ruleSetName);
    return executeRules(rules, userData);
  }

  /**
   * Evaluates the given business rules. Intended to be used if business rules are created
   * programmatically instead of the rules-engine DSL
   *
   * @param rules
   * @param userData
   * @return
   */
  public CompletableFuture<List<RuleEvaluationResult>> executeRules(
      final Rules rules, Map<?, ?> userData) {
    return fetchDataAndEvaluateRules(rules, userData, RulesEvaluator.Options.PRE_CONDITIONS_ONLY)
        .thenApply(preEvalResult -> filterRulesToEvaluate(rules, preEvalResult))
        .thenCompose(
            filteredRules ->
                fetchDataAndEvaluateRules(
                    filteredRules, userData, RulesEvaluator.Options.CONDITIONS_ONLY));
  }

  protected CompletableFuture<List<RuleEvaluationResult>> fetchDataAndEvaluateRules(
      Rules rulesToEvaluate, Map<?, ?> userData, RulesEvaluator.Options options) {
    LOG.debug("{}: Fetching data for rules: {}", options, rulesToEvaluate.getFeatures());
    return dataService
        .fetchData(getParametersByDataSource(rulesToEvaluate, options), userData)
        .thenApply(dataSet -> evaluateRules(dataSet, rulesToEvaluate, options));
  }

  /**
   * Given all rules, filter rules that either have the pre-condition satisfied or does not have a
   * pre-condition.
   *
   * @param preEvalResult
   * @param rules
   * @return
   */
  protected Rules filterRulesToEvaluate(
      final Rules rules, final List<RuleEvaluationResult> preEvalResult) {
    return new Rules(
        rules.getFeatures().stream()
            .filter(hasPreConditionSatisfied(preEvalResult))
            .collect(Collectors.toList()));
  }

  /**
   * Predicate to determine if pre-condition has been met (by evaluating the pre-condition). If
   * there are no pre-conditions, consider the rule satisfied.
   *
   * @param preEvalResult
   * @return
   */
  private Predicate<Feature> hasPreConditionSatisfied(List<RuleEvaluationResult> preEvalResult) {
    return feature ->
        isEmpty(feature.getRequirements().getPreConditions())
            || preEvalResult.stream()
                .filter(RuleEvaluationResult::hasMatch)
                .map(RuleEvaluationResult::getFeature)
                .anyMatch(matched -> matched.equalsIgnoreCase(feature.getName()));
  }

  /**
   * Iterate through all rules and extract set of parameters required for evaluating the rules
   * Return type is a map keyed/grouped by data source and list of parameters from that data source.
   *
   * @param rules
   * @param options
   * @return
   */
  protected Map<String, List<ParameterKey>> getParametersByDataSource(
      final Rules rules, final RulesEvaluator.Options options) {
    return rules.getFeatures().stream()
        .flatMap(feature -> conditionsByType(feature.getRequirements(), options))
        .map(condition -> new ParameterKey(condition.getKey()))
        .distinct()
        .collect(Collectors.groupingBy(ParameterKey::getDataSource));
  }

  private Stream<EvaluationCondition> conditionsByType(
      final Requirements requirements, final RulesEvaluator.Options options) {
    if (options == RulesEvaluator.Options.PRE_CONDITIONS_ONLY) {
      return requirements.getPreConditions().stream();
    } else {
      return requirements.getConditions().stream();
    }
  }

  private List<RuleEvaluationResult> evaluateRules(
      DataSet dataSet, Rules rules, RulesEvaluator.Options options) {
    RulesEvaluator evaluator = new RulesEvaluator(dataSet, rules, options);
    List<RuleEvaluationResult> ruleEvaluationResults = evaluator.evaluate();
    LOG.debug(
        "{}: Rules Evaluated: {} \nResult: {}",
        options,
        rules.getFeatures(),
        ruleEvaluationResults);
    return ruleEvaluationResults;
  }
}
