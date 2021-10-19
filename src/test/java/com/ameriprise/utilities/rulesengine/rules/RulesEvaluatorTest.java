/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine.rules;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ameriprise.utilities.rulesengine.datasources.models.DataSet;
import com.ameriprise.utilities.rulesengine.rules.models.EvaluationCondition;
import com.ameriprise.utilities.rulesengine.rules.models.Parameter;
import com.ameriprise.utilities.rulesengine.rules.models.RuleEvaluationResult;
import com.ameriprise.utilities.rulesengine.rules.models.Rules;

@RunWith(JUnit4.class)
public class RulesEvaluatorTest {

  @Test
  public void testEvaluate_AllPositive() {

    // given
    DataSet dataSet = new DataSet();
    addMockTelephoneData(dataSet);
    addMockRegistrationData(dataSet);
    addMockAnniversaryData(dataSet);
    addMockCreditCardData(dataSet);
    RulesEvaluator rulesEvaluator = new RulesEvaluator(dataSet, mockRules());

    // when
    List<RuleEvaluationResult> result = rulesEvaluator.evaluate();

    // then
    assertNotNull(result);
    assertEquals(3, result.size());

    result.forEach(
        evalResult -> {
          assertTrue(
              evalResult.getReturnValue().equals("Y")
                  || evalResult.getReturnValue().equals("true"));
          evalResult
              .getMatched()
              .forEach(
                  matchResult -> {
                    assertNotNull(matchResult.getCondition().getKey());
                    assertTrue(matchResult.getParameters().size() > 0);
                  });
        });
  }

  @Test
  public void testEvaluate_AllNegative() {

    // given
    DataSet dataSet = new DataSet();
    RulesEvaluator rulesEvaluator = new RulesEvaluator(dataSet, mockRules());

    // when
    List<RuleEvaluationResult> result = rulesEvaluator.evaluate();

    // then
    assertNotNull(result);
    assertEquals(3, result.size());

    result.forEach(
        evalResult -> {
          assertTrue(
              evalResult.getReturnValue().equals("N")
                  || evalResult.getReturnValue().equals("false"));
          assertEquals(0, evalResult.getMatched().size());
        });
  }

  @Test
  public void testEvaluateCondition_MatchOneOf_Positive() {
    // given
    DataSet dataSet = new DataSet();
    addMockTelephoneData(dataSet);
    RulesEvaluator engine = new RulesEvaluator(dataSet, new Rules());

    EvaluationCondition condition =
        new EvaluationCondition("client-telephones:sms-enrollment-status");
    condition.setOneOf(Arrays.asList("ENROLLED", "UNENROLLED"));

    // when
    boolean result = engine.evaluateCondition(condition);

    // then
    assertTrue(result);
  }

  @Test
  public void testEvaluateCondition_MatchOneOf_Positive_WithNull() {
    // given
    DataSet dataSet = new DataSet();
    addMockTelephoneData(dataSet);
    RulesEvaluator engine = new RulesEvaluator(dataSet, new Rules());

    EvaluationCondition condition =
        new EvaluationCondition("client-telephones:sms-enrollment-status");
    condition.setOneOf(Arrays.asList("ABC", "XYZ", "null"));

    // when
    boolean result = engine.evaluateCondition(condition);

    // then
    assertTrue(result);
  }

  @Test
  public void testEvaluateCondition_MatchOneOf_Negative() {
    // given
    DataSet dataSet = new DataSet();
    addMockTelephoneData(dataSet);
    RulesEvaluator engine = new RulesEvaluator(dataSet, new Rules());

    EvaluationCondition condition =
        new EvaluationCondition("client-telephones:sms-enrollment-status");
    condition.setOneOf(Arrays.asList("ABC", "XYZ", "UVX"));

    // when
    boolean result = engine.evaluateCondition(condition);

    // then
    assertFalse(result);
  }

  @Test
  public void testEvaluateCondition_MatchNotOneOf_Positive() {
    // given
    DataSet dataSet = new DataSet();
    addMockTelephoneData(dataSet);
    RulesEvaluator engine = new RulesEvaluator(dataSet, new Rules());

    EvaluationCondition condition =
        new EvaluationCondition("client-telephones:sms-enrollment-status");
    condition.setNotOneOf(Arrays.asList("ENROLLED"));

    // when
    boolean result = engine.evaluateCondition(condition);

    // then
    assertTrue(result);
  }

  @Test
  public void testEvaluateCondition_MatchNotOneOf_Negative() {
    // given
    DataSet dataSet = new DataSet();
    addMockTelephoneData(dataSet);
    RulesEvaluator engine = new RulesEvaluator(dataSet, new Rules());

    EvaluationCondition condition =
        new EvaluationCondition("client-telephones:sms-enrollment-status");
    condition.setNotOneOf(Arrays.asList("null"));

    // when
    boolean result = engine.evaluateCondition(condition);

    // then
    assertFalse(result);
  }

  @Test
  public void testEvaluateCondition_MatchEquals() {
    // given
    DataSet dataSet = new DataSet();
    addMockAnniversaryData(dataSet);
    RulesEvaluator engine = new RulesEvaluator(dataSet, new Rules());

    EvaluationCondition condition =
        new EvaluationCondition("advice-insights:anniversary-milestone-number");
    condition.setEquals("15");

    // when
    boolean result = engine.evaluateCondition(condition);

    // then
    assertTrue(result);
  }

  @Test
  public void testEvaluateCondition_MatchNotEquals() {
    // given
    DataSet dataSet = new DataSet();
    addMockAnniversaryData(dataSet);
    RulesEvaluator engine = new RulesEvaluator(dataSet, new Rules());

    EvaluationCondition condition =
        new EvaluationCondition("advice-insights:anniversary-milestone-number");
    condition.setNotEquals("100");

    // when
    boolean result = engine.evaluateCondition(condition);

    // then
    assertTrue(result);
  }

  @Test
  public void testEvaluateCondition_MatchContains() {
    // given
    DataSet dataSet = new DataSet();
    addMockAnniversaryData(dataSet);
    RulesEvaluator engine = new RulesEvaluator(dataSet, new Rules());

    EvaluationCondition condition =
        new EvaluationCondition("advice-insights:anniversary-milestone-number");
    condition.setContains("5");

    // when
    boolean result = engine.evaluateCondition(condition);

    // then
    assertTrue(result);
  }

  @Test
  public void testEvaluateCondition_MatchNotContains() {
    // given
    DataSet dataSet = new DataSet();
    addMockAnniversaryData(dataSet);
    RulesEvaluator engine = new RulesEvaluator(dataSet, new Rules());

    EvaluationCondition condition =
        new EvaluationCondition("advice-insights:anniversary-milestone-number");
    condition.setNotContains("7");

    // when
    boolean result = engine.evaluateCondition(condition);

    // then
    assertTrue(result);
  }

  @Test
  public void testEvaluateCondition_MatchWithin_Positive() {
    // given
    DataSet dataSet = new DataSet();
    addMockRegistrationData(dataSet);
    RulesEvaluator engine = new RulesEvaluator(dataSet, new Rules());

    EvaluationCondition condition =
        new EvaluationCondition("registration:enrollment.SCS_SITE.action-date");
    condition.setWithinDays(180);

    // when
    boolean result = engine.evaluateCondition(condition);

    // then
    assertTrue(result);
  }

  @Test
  public void testEvaluateCondition_MatchWithin_Negative() {
    // given
    DataSet dataSet = new DataSet();
    addMockRegistrationData(dataSet);
    RulesEvaluator engine = new RulesEvaluator(dataSet, new Rules());

    EvaluationCondition condition =
        new EvaluationCondition("registration:enrollment.SCS_SITE.action-date");
    condition.setWithinDays(5);

    // when
    boolean result = engine.evaluateCondition(condition);

    // then
    assertFalse(result);
  }

  @Test
  public void testEvaluateCondition_MatchBeyond_Positive() {
    // given
    DataSet dataSet = new DataSet();
    addMockRegistrationData(dataSet);
    RulesEvaluator engine = new RulesEvaluator(dataSet, new Rules());

    EvaluationCondition condition =
        new EvaluationCondition("registration:enrollment.SCS_SITE.action-date");
    condition.setBeyondDays(5);

    // when
    boolean result = engine.evaluateCondition(condition);

    // then
    assertTrue(result);
  }

  @Test
  public void testEvaluateCondition_MatchBeyond_Negative() {
    // given
    DataSet dataSet = new DataSet();
    addMockRegistrationData(dataSet);
    RulesEvaluator engine = new RulesEvaluator(dataSet, new Rules());

    EvaluationCondition condition =
        new EvaluationCondition("registration:enrollment.SCS_SITE.action-date");
    condition.setBeyondDays(180);

    // when
    boolean result = engine.evaluateCondition(condition);

    // then
    assertFalse(result);
  }

  @Test
  public void testEvaluateCondition_MatchBeyondNoData_Negative_1() {
    // given
    DataSet dataSet = new DataSet();
    RulesEvaluator engine = new RulesEvaluator(dataSet, new Rules());

    EvaluationCondition condition =
        new EvaluationCondition("registration:enrollment.SCS_SITE.action-date");
    condition.setBeyondDays(180);

    // when
    boolean result = engine.evaluateCondition(condition);

    // then
    assertFalse(result);
  }

  @Test
  public void testEvaluateCondition_MatchWithinNoData_Negative_2() {
    // given
    DataSet dataSet = new DataSet();
    RulesEvaluator engine = new RulesEvaluator(dataSet, new Rules());

    EvaluationCondition condition =
        new EvaluationCondition("registration:enrollment.SCS_SITE.action-date");
    condition.setWithinDays(180);

    // when
    boolean result = engine.evaluateCondition(condition);

    // then
    assertFalse(result);
  }

  private void addMockTelephoneData(DataSet dataSet) {
    dataSet.addParameter(new Parameter("client-telephones:sms-enrollment-status", "UNENROLLED"));
    dataSet.addParameter(new Parameter("client-telephones:sms-enrollment-status", "UNENROLLED"));
    dataSet.addParameter(new Parameter("client-telephones:sms-enrollment-status", null));
  }

  private void addMockRegistrationData(DataSet dataSet) {
    dataSet.addParameter(
        new Parameter(
            "registration:enrollment.SCS_SITE.action-date",
            LocalDate.now().minusDays(91).toString()));
  }

  private void addMockAnniversaryData(DataSet dataSet) {
    dataSet.addParameter(new Parameter("advice-insights:anniversary-milestone-number", "15"));
  }

  private void addMockCreditCardData(DataSet dataSet) {
    dataSet.addParameter(new Parameter("advice-insights:credit-card-promotion", "Y"));
  }

  private Rules mockRules() {
    return new ClassPathRulesLoader().load("notification-business-rules");
  }
}
