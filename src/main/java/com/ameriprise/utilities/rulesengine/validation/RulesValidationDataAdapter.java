/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine.validation;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ameriprise.utilities.rulesengine.RulesEngine;
import com.ameriprise.utilities.rulesengine.datasources.DataFetchService;
import com.ameriprise.utilities.rulesengine.datasources.DataSourceAdaptor;
import com.ameriprise.utilities.rulesengine.datasources.models.DataFetchingContext;
import com.ameriprise.utilities.rulesengine.datasources.models.DataSet;
import com.ameriprise.utilities.rulesengine.rules.models.Parameter;
import com.ameriprise.utilities.rulesengine.rules.models.RuleEvaluationResult;
import com.ameriprise.utilities.rulesengine.rules.models.Rules;

/**
 * Class intended for testing a set of business rules using the rules engine. Executes the business
 * rules using mock data and returns the result of execution as a data set.
 */
@Component("rules-validator-data-source-adaptor")
public class RulesValidationDataAdapter implements DataSourceAdaptor {

  private RulesEngine rulesEngine;

  private static final Logger LOG = LoggerFactory.getLogger(RulesValidationDataAdapter.class);

  @Autowired
  public RulesValidationDataAdapter(
      @Qualifier("testDataFetchService") DataFetchService dataFetchService) {
    rulesEngine = new RulesEngine(dataFetchService, null);
  }

  /**
   * Executes the business rules under test and returns the result as a data set
   *
   * @param dfe
   * @return
   */
  @Override
  public CompletableFuture<DataSet> fetch(DataFetchingContext dfe) {
    Rules rulesToEvaluate = (Rules) dfe.getUserData().get("rules");
    return rulesEngine
        .executeRules(rulesToEvaluate, dfe.getUserData())
        .thenApply(this::resultsAsDataSet);
  }

  private DataSet resultsAsDataSet(List<RuleEvaluationResult> results) {
    return results.stream()
        .flatMap(this::toDataSet)
        .collect(DataSet::new, DataSet::addParameter, (d1, d2) -> {});
  }

  private Stream<? extends Parameter> toDataSet(RuleEvaluationResult result) {
    return Stream.of(
        new Parameter("rules-validator:rule-execution-result.returnValue", result.getReturnValue()),
        new Parameter("rules-validator:rule-execution-result.feature-name", result.getFeature()));
  }
}
