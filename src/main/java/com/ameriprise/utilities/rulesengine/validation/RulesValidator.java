/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine.validation;

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

@Component("rules-validator")
public class RulesValidator implements DataSourceAdaptor {

  private RulesEngine rulesEngine;

  private static final Logger LOG = LoggerFactory.getLogger(RulesValidator.class);

  @Autowired
  public RulesValidator(@Qualifier("testDataFetchService") DataFetchService dataFetchService) {
    rulesEngine = new RulesEngine(dataFetchService, null);
  }

  @Override
  public CompletableFuture<DataSet> fetch(DataFetchingContext dfe) {
    Rules rulesToEvaluate = (Rules) dfe.getUserData().get("rules");
    return rulesEngine
        .executeRules(rulesToEvaluate, dfe.getUserData())
        .thenApply(
            results ->
                results.stream()
                    .filter(RuleEvaluationResult::hasMatch)
                    .flatMap(
                        result ->
                            Stream.of(
                                new Parameter(
                                    "rules-validator:rule-execution-result.returnValue",
                                    result.getReturnValue()),
                                new Parameter(
                                    "rules-validator:rule-execution-result.feature-name",
                                    result.getFeature())))
                    .collect(DataSet::new, DataSet::addParameter, (d1, d2) -> {}));
  }
}
