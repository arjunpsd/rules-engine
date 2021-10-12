/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine.rules;

import static java.util.Objects.isNull;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.ameriprise.utilities.rulesengine.rules.models.Parameter;

public class Matchers {
  public static boolean isEqualTo(final String option, final Parameter parameter) {
    return option.equalsIgnoreCase(getValue(parameter));
  }

  public static boolean isNotEqualTo(final String option, final Parameter parameter) {
    return !option.equalsIgnoreCase(getValue(parameter));
  }

  public static boolean isWithinDays(long numberOfDays, final Parameter parameter) {
    LocalDate givenDate = LocalDate.parse(getValue(parameter, "2999-12-31"));
    LocalDate now = LocalDate.now();
    return Math.abs(ChronoUnit.DAYS.between(now, givenDate)) <= numberOfDays;
  }

  public static boolean isBeyondDays(long numberOfDays, final Parameter parameter) {
    LocalDate now = LocalDate.now();
    LocalDate givenDate = LocalDate.parse(getValue(parameter, now.toString()));
    return Math.abs(ChronoUnit.DAYS.between(givenDate, now)) > numberOfDays;
  }

  public static boolean isOneOf(final List<String> options, final Parameter parameter) {
    return options.stream()
        .map(String::toLowerCase)
        .anyMatch(givenValue -> givenValue.equals(getValue(parameter)));
  }

  public static boolean isNotOneOf(final List<String> options, final Parameter parameter) {
    return options.stream()
        .map(String::toLowerCase)
        .noneMatch(givenValue -> givenValue.equals(getValue(parameter)));
  }

  public static boolean contains(final String option, final Parameter parameter) {
    return getValue(parameter).contains(option);
  }

  public static boolean notContains(final String option, final Parameter parameter) {
    return !getValue(parameter).contains(option);
  }

  private static String getValue(Parameter parameter) {
    return getValue(parameter, "null");
  }

  private static String getValue(Parameter parameter, String valueIfNull) {
    return isNull(parameter.getDataValue()) ? valueIfNull : parameter.getDataValue().toLowerCase();
  }
}
