/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine.validation;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import java.util.concurrent.CompletableFuture;

import com.ameriprise.utilities.rulesengine.rules.models.RuleEvaluationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ameriprise.utilities.rulesengine.RulesEngine;
import com.ameriprise.utilities.rulesengine.datasources.DataFetchService;
import com.ameriprise.utilities.rulesengine.datasources.DataSourceAdaptor;
import com.ameriprise.utilities.rulesengine.datasources.models.DataFetchingContext;
import com.ameriprise.utilities.rulesengine.datasources.models.DataSet;
import com.ameriprise.utilities.rulesengine.rules.models.Parameter;
import com.ameriprise.utilities.rulesengine.rules.models.Rules;

@Component("rules-validator")
public class RulesValidator implements DataSourceAdaptor {

  private RulesEngine rulesEngine;

  @Autowired
  public RulesValidator(@Qualifier("testDataFetchService") DataFetchService dataFetchService) {
    rulesEngine = new RulesEngine(dataFetchService, null);
  }

  @Override
  public CompletableFuture<DataSet> fetch(DataFetchingContext dfe) {
    Rules rulesToEvaluate = (Rules) dfe.getUserData().get("rules");
    Rules tests = (Rules) dfe.getUserData().get("tests");
    return rulesEngine
        .executeRules(rulesToEvaluate, dfe.getUserData())
        .thenApply(
            results ->
                results.stream()
                    .filter(RuleEvaluationResult::hasMatch)
                    .map(
                        result ->
                            new Parameter(
                                "rules-validator:rule-execution-result.returnValue",
                                result.getReturnValue()))
                    .collect(DataSet::new, DataSet::addParameter, (d1, d2) -> {}));
  }
}
