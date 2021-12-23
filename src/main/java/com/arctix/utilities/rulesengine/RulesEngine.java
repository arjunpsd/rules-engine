/* Use of this source code is subject to terms of MIT license.
 @author: Arjun Prasad
 @license: MIT
 @year: 2021 */
package com.arctix.utilities.rulesengine;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import com.arctix.utilities.rulesengine.datasources.DataFetchService;
import com.arctix.utilities.rulesengine.datasources.models.DataFetchResult;
import com.arctix.utilities.rulesengine.rules.RulesEvaluator;
import com.arctix.utilities.rulesengine.rules.RulesLoader;
import com.arctix.utilities.rulesengine.rules.models.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
      final String ruleSetName, final Map<?, ?> userData) {
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
      final Rules rules, final Map<?, ?> userData) {
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
        .thenApply(result -> evaluateRules(result, rulesToEvaluate, options));
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
  protected Predicate<Feature> hasPreConditionSatisfied(List<RuleEvaluationResult> preEvalResult) {
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

  protected Stream<EvaluationCondition> conditionsByType(
      final Requirements requirements, final RulesEvaluator.Options options) {
    if (options == RulesEvaluator.Options.PRE_CONDITIONS_ONLY) {
      return requirements.getPreConditions().stream();
    } else if (options == RulesEvaluator.Options.CONDITIONS_ONLY) {
      return requirements.getConditions().stream();
    } else {
      return requirements.getPostConditions().stream();
    }
  }

  protected List<RuleEvaluationResult> evaluateRules(
      final DataFetchResult dataFetchResult,
      final Rules rules,
      final RulesEvaluator.Options options) {

    List<Feature> featuresWithErrors = getFeaturesWithErrors(rules, dataFetchResult, options);
    List<Feature> featuresToEvaluate = new ArrayList<>(rules.getFeatures());
    if (isNotEmpty(featuresWithErrors)) {
      LOG.warn(
          "The following features will not be evaluated due to errors in fetching data from data sources: {}",
          featuresWithErrors);
      featuresToEvaluate.removeAll(featuresWithErrors);
      LOG.debug("Features to be evaluated: {}", featuresToEvaluate);
    }

    RulesEvaluator evaluator =
        new RulesEvaluator(dataFetchResult.getDataSet(), new Rules(featuresToEvaluate), options);
    List<RuleEvaluationResult> ruleEvaluationResults = evaluator.evaluate();
    LOG.debug(
        "{}: Rules Evaluated: {} \nResult: {}",
        options,
        rules.getFeatures(),
        ruleEvaluationResults);
    return ruleEvaluationResults;
  }

  protected List<Feature> getFeaturesWithErrors(
      final Rules allRules,
      final DataFetchResult dataFetchResult,
      final RulesEvaluator.Options options) {
    final Set<String> dataSourceNamesWithExceptions =
        dataFetchResult.getDataSourceWithExceptions().keySet();
    return allRules.getFeatures().stream()
        .filter(
            feature -> hasExceptionsForCondition(feature, dataSourceNamesWithExceptions, options))
        .collect(Collectors.toList());
  }

  protected boolean hasExceptionsForCondition(
      final Feature feature,
      final Set<String> dataSourceWithExceptions,
      RulesEvaluator.Options options) {
    return conditionsByType(feature.getRequirements(), options)
        .anyMatch(
            condition ->
                dataSourceWithExceptions.contains(
                    new ParameterKey(condition.getKey()).getDataSource()));
  }
}
