/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine;

import static java.util.Objects.nonNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.ameriprise.utilities.rulesengine.datasources.DataFetchService;
import com.ameriprise.utilities.rulesengine.datasources.models.DataFetchResult;
import com.ameriprise.utilities.rulesengine.datasources.models.DataSet;
import com.ameriprise.utilities.rulesengine.rules.RulesEvaluator;
import com.ameriprise.utilities.rulesengine.rules.RulesLoader;
import com.ameriprise.utilities.rulesengine.rules.models.*;

@RunWith(MockitoJUnitRunner.class)
public class RulesEngineTest extends AbstractTest {

  @Mock RulesLoader rulesLoader;

  @Mock DataFetchService dataService;

  @InjectMocks RulesEngine rulesEngine;

  @Before
  public void setup() {
    when(rulesLoader.load(anyString())).thenReturn(mockedRules());
  }

  @Test
  public void testExecuteRules_AllPass() {

    // given
    when(dataService.fetchData(anyMap(), anyMap()))
        .thenReturn(CompletableFuture.completedFuture(mockDataSet("p-one", "c-two", "c-three")));

    // when
    List<RuleEvaluationResult> result =
        rulesEngine.executeRules("fake-rules", new HashMap<>()).join();

    // then
    assertNotNull(result);
    assertEquals(2, result.size());
    verify(dataService, times(2)).fetchData(anyMap(), anyMap());
  }

  @Test
  public void testExecuteRules_FailPreCondition() {

    // given
    when(dataService.fetchData(anyMap(), anyMap()))
        .thenReturn(CompletableFuture.completedFuture(mockDataSet("p-fail", "c-two", "c-three")));

    // when
    List<RuleEvaluationResult> result =
        rulesEngine.executeRules("fake-rules", new HashMap<>()).join();

    // then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("feature2", result.get(0).getFeature());
    verify(dataService, times(2)).fetchData(anyMap(), anyMap());
  }

  @Test
  public void testExecuteRules_FailCondition1() {

    // given
    when(dataService.fetchData(anyMap(), anyMap()))
        .thenReturn(CompletableFuture.completedFuture(mockDataSet("p-one", "c-fail", "c-three")));

    // when
    List<RuleEvaluationResult> result =
        rulesEngine.executeRules("fake-rules", new HashMap<>()).join();

    // then
    assertNotNull(result);
    assertEquals(2, result.size());
    result.forEach(evalResult -> assertEquals("false", evalResult.getReturnValue()));
    verify(dataService, times(2)).fetchData(anyMap(), anyMap());
  }

  @Test
  public void testExecuteRules_FailCondition2() {

    // given
    when(dataService.fetchData(anyMap(), anyMap()))
        .thenReturn(CompletableFuture.completedFuture(mockDataSet("p-one", "c-two", "c-fail")));

    // when
    List<RuleEvaluationResult> result =
        rulesEngine.executeRules("fake-rules", new HashMap<>()).join();

    // then
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("true", result.get(0).getReturnValue());
    assertEquals("false", result.get(1).getReturnValue());
    assertEquals("feature1", result.get(0).getFeature());
  }

  @Test
  public void testGetAttributesBySource_ForCondition() {
    // given
    Rules rules = mockedRules();

    // when
    Map<String, List<ParameterKey>> result =
        rulesEngine.getParametersByDataSource(rules, RulesEvaluator.Options.CONDITIONS_ONLY);

    // then
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(1, result.get("ds2").size());
    assertEquals(1, result.get("ds3").size());
  }

  @Test
  public void testGetAttributesBySource_ForPreCondition() {
    // given
    Rules rules = mockedRules();

    // when
    Map<String, List<ParameterKey>> result =
        rulesEngine.getParametersByDataSource(rules, RulesEvaluator.Options.PRE_CONDITIONS_ONLY);

    // then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(1, result.get("ds1").size());
  }

  @Test
  public void testGetFeaturesWithErrors() {

    // given
    Rules rules = mockedRules();
    DataFetchResult dataFetchResult =
        mockDataSet("p-one", "c-two", "c-three", new RuntimeException());

    // when
    List<Feature> result =
        rulesEngine.getFeaturesWithErrors(
            rules, dataFetchResult, RulesEvaluator.Options.CONDITIONS_ONLY);

    // then
    assertEquals(1, result.size());
  }

  @Test
  public void testEvaluateRulesWithErrors() {

    // given
    Rules rules = mockedRules();
    DataFetchResult dataFetchResult =
        mockDataSet("p-one", "c-two", "c-three", new RuntimeException());

    // when
    List<RuleEvaluationResult> result =
        rulesEngine.evaluateRules(dataFetchResult, rules, RulesEvaluator.Options.CONDITIONS_ONLY);

    // then
    assertEquals(1, result.size());
  }

  private Rules mockedRules() {

    EvaluationCondition preCondition1 = new EvaluationCondition();
    preCondition1.setType("data");
    preCondition1.setKey("ds1:attribute1");
    preCondition1.setEquals("p-one");

    EvaluationCondition condition1 = new EvaluationCondition();
    condition1.setType("data");
    condition1.setKey("ds2:attribute2");
    condition1.setEquals("c-two");

    EvaluationCondition condition2 = new EvaluationCondition();
    condition2.setType("data");
    condition2.setKey("ds3:attribute3");
    condition2.setEquals("c-three");

    Feature feature1 =
        new Feature(
            "feature1", new Requirements(Arrays.asList(preCondition1), Arrays.asList(condition1)));

    Feature feature2 =
        new Feature(
            "feature2", new Requirements(new ArrayList<>(), Arrays.asList(condition1, condition2)));

    return new Rules(Arrays.asList(feature1, feature2));
  }

  private DataFetchResult mockDataSet(String attr1, String attr2, String attr3) {
    return mockDataSet(attr1, attr2, attr3, null);
  }

  private DataFetchResult mockDataSet(String attr1, String attr2, String attr3, Throwable t) {
    DataSet dataSet = new DataSet();
    dataSet.addParameter(new Parameter("ds1:attribute1", attr1));
    dataSet.addParameter(new Parameter("ds2:attribute2", attr2));
    if (nonNull(t)) {
      return new DataFetchResult.Builder().addDataSet(dataSet).addException("ds3", t).build();
    } else {
      dataSet.addParameter(new Parameter("ds3:attribute3", attr3));
      return new DataFetchResult.Builder().addDataSet(dataSet).build();
    }
  }
}
