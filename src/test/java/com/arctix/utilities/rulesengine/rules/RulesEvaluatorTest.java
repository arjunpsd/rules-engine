/* Use of this source code is subject to terms of MIT license.
 @author: Arjun Prasad
 @license: MIT
 @year: 2021 */
package com.arctix.utilities.rulesengine.rules;

import static org.junit.Assert.*;

import com.arctix.utilities.rulesengine.datasources.models.DataSet;
import com.arctix.utilities.rulesengine.rules.models.EvaluationCondition;
import com.arctix.utilities.rulesengine.rules.models.Parameter;
import com.arctix.utilities.rulesengine.rules.models.RuleEvaluationResult;
import com.arctix.utilities.rulesengine.rules.models.Rules;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class RulesEvaluatorTest {

  @Test
  public void testEvaluate_AllPositive() {

    // given
    DataSet dataSet = new DataSet();
    addMockRuleEvaluationData(dataSet);
    addMockRegistrationData(dataSet);
    RulesEvaluator rulesEvaluator = new RulesEvaluator(dataSet, mockRules());

    // when
    List<RuleEvaluationResult> result = rulesEvaluator.evaluate();

    // then
    assertNotNull(result);
    assertEquals(1, result.size());

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
    assertEquals(1, result.size());

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
    addMockRegistrationData(dataSet);
    RulesEvaluator engine = new RulesEvaluator(dataSet, new Rules());

    EvaluationCondition condition = new EvaluationCondition("user-profile:badge-level");
    condition.setOneOf(Arrays.asList("SILVER", "BRONZE"));

    // when
    boolean result = engine.evaluateCondition(condition);

    // then
    assertTrue(result);
  }

  @Test
  public void testEvaluateCondition_MatchOneOf_Positive_WithNull() {
    // given
    DataSet dataSet = new DataSet();
    addMockRegistrationData(dataSet);
    RulesEvaluator engine = new RulesEvaluator(dataSet, new Rules());

    EvaluationCondition condition = new EvaluationCondition("user-profile:badge-level");
    condition.setOneOf(Arrays.asList("BRONZE", "XYZ", "null"));

    // when
    boolean result = engine.evaluateCondition(condition);

    // then
    assertTrue(result);
  }

  @Test
  public void testEvaluateCondition_MatchOneOf_Negative() {
    // given
    DataSet dataSet = new DataSet();
    addMockRegistrationData(dataSet);
    RulesEvaluator engine = new RulesEvaluator(dataSet, new Rules());

    EvaluationCondition condition = new EvaluationCondition("user-profile:badge-level");
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
    addMockRegistrationData(dataSet);
    RulesEvaluator engine = new RulesEvaluator(dataSet, new Rules());

    EvaluationCondition condition = new EvaluationCondition("user-profile:badge-level");
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
    addMockRegistrationData(dataSet);
    RulesEvaluator engine = new RulesEvaluator(dataSet, new Rules());

    EvaluationCondition condition = new EvaluationCondition("user-profile:badge-level");
    condition.setNotOneOf(Arrays.asList("BRONZE"));

    // when
    boolean result = engine.evaluateCondition(condition);

    // then
    assertFalse(result);
  }

  @Test
  public void testEvaluateCondition_MatchWithin_Positive() {
    // given
    DataSet dataSet = new DataSet();
    addMockRegistrationData(dataSet);
    RulesEvaluator engine = new RulesEvaluator(dataSet, new Rules());

    EvaluationCondition condition = new EvaluationCondition("user-profile:registration-date");
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

    EvaluationCondition condition = new EvaluationCondition("user-profile:registration-date");
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

    EvaluationCondition condition = new EvaluationCondition("user-profile:registration-date");
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

    EvaluationCondition condition = new EvaluationCondition("user-profile:registration-date");
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

    EvaluationCondition condition = new EvaluationCondition("user-profile:registration-date");
    condition.setBeyondDays(180);

    // when
    boolean result = engine.evaluateCondition(condition);

    // then
    assertTrue(result);
  }

  @Test
  public void testEvaluateCondition_MatchWithinNoData_Negative_2() {
    // given
    DataSet dataSet = new DataSet();
    RulesEvaluator engine = new RulesEvaluator(dataSet, new Rules());

    EvaluationCondition condition = new EvaluationCondition("user-profile:registration-date");
    condition.setWithinDays(180);

    // when
    boolean result = engine.evaluateCondition(condition);

    // then
    assertFalse(result);
  }

  @Test
  public void testEvaluateCondition_MatchAfter_Positive() {
    // given
    DataSet dataSet = new DataSet();
    addMockSystemData(dataSet);
    RulesEvaluator engine = new RulesEvaluator(dataSet, new Rules());

    EvaluationCondition condition = new EvaluationCondition("system-data:date-time");
    condition.setAfter("2021-01-31T00:00:00Z");

    // when
    boolean result = engine.evaluateCondition(condition);

    // then
    assertTrue(result);
  }

  @Test
  public void testEvaluateCondition_MatchAfter_Negative() {
    // given
    DataSet dataSet = new DataSet();
    addMockSystemData(dataSet);
    RulesEvaluator engine = new RulesEvaluator(dataSet, new Rules());

    EvaluationCondition condition = new EvaluationCondition("system-data:date-time");
    condition.setAfter("2045-01-31T00:00:00Z");

    // when
    boolean result = engine.evaluateCondition(condition);

    // then
    assertFalse(result);
  }

  @Test
  public void testEvaluateCondition_MatchBefore_Positive() {
    // given
    DataSet dataSet = new DataSet();
    addMockSystemData(dataSet);
    RulesEvaluator engine = new RulesEvaluator(dataSet, new Rules());

    EvaluationCondition condition = new EvaluationCondition("system-data:date-time");
    condition.setBefore("2045-01-31T00:00:00Z");

    // when
    boolean result = engine.evaluateCondition(condition);

    // then
    assertTrue(result);
  }

  @Test
  public void testEvaluateCondition_MatchBefore_Negative() {
    // given
    DataSet dataSet = new DataSet();
    addMockSystemData(dataSet);
    RulesEvaluator engine = new RulesEvaluator(dataSet, new Rules());

    EvaluationCondition condition = new EvaluationCondition("system-data:date-time");
    condition.setBefore("2021-01-31T00:00:00Z");

    // when
    boolean result = engine.evaluateCondition(condition);

    // then
    assertFalse(result);
  }

  private void addMockRuleEvaluationData(DataSet dataSet) {
    dataSet.addParameter(
        new Parameter(
            "notification-rules:mailing-list-notification.last-evaluation-date",
            "2021-01-01T00:00:00Z"));
  }

  private void addMockRegistrationData(DataSet dataSet) {
    dataSet.addParameter(
        new Parameter("user-profile:registration-date", LocalDate.now().minusDays(91).toString()));
    dataSet.addParameter(new Parameter("user-profile:badge-level", "BRONZE"));
  }

  private void addMockSystemData(DataSet dataSet) {
    dataSet.addParameter(
        new Parameter(
            "system-data:date-time",
            ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
  }

  private Rules mockRules() {
    return new RulesFileLoader().load("notification-business-rules");
  }
}
