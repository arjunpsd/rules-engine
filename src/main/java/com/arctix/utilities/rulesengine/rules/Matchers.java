/* Use of this source code is subject to terms of MIT license.
 @author: Arjun Prasad
 @license: MIT
 @year: 2021 */
package com.arctix.utilities.rulesengine.rules;

import static com.arctix.utilities.rulesengine.utils.DateTimeFormat.parseIsoDateTime;
import static java.util.Objects.isNull;

import com.arctix.utilities.rulesengine.rules.models.Parameter;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class Matchers {
  public static boolean isEqualTo(final String option, final Parameter parameter) {
    return option.equalsIgnoreCase(getValue(parameter));
  }

  public static boolean isNotEqualTo(final String option, final Parameter parameter) {
    return !option.equalsIgnoreCase(getValue(parameter));
  }

  public static boolean isWithinDays(long numberOfDays, final Parameter parameter) {
    ZonedDateTime now = ZonedDateTime.now();
    ZonedDateTime givenDate = getValue(parameter, now.plusYears(100));
    return Math.abs(ChronoUnit.DAYS.between(now, givenDate)) <= numberOfDays;
  }

  public static boolean isBeyondDays(long numberOfDays, final Parameter parameter) {
    ZonedDateTime now = ZonedDateTime.now();
    ZonedDateTime givenDate = getValue(parameter, now.plusYears(100));
    return Math.abs(ChronoUnit.DAYS.between(givenDate, now)) > numberOfDays;
  }

  public static boolean isOneOf(final List<String> options, final Parameter parameter) {
    return options.stream()
        .anyMatch(givenValue -> givenValue.equalsIgnoreCase(getValue(parameter)));
  }

  public static boolean isNotOneOf(final List<String> options, final Parameter parameter) {
    return options.stream()
        .noneMatch(givenValue -> givenValue.equalsIgnoreCase(getValue(parameter)));
  }

  public static boolean contains(final String option, final Parameter parameter) {
    return getValue(parameter).contains(option);
  }

  public static boolean notContains(final String option, final Parameter parameter) {
    return !getValue(parameter).contains(option);
  }

  public static boolean isAfter(final String givenDate, final Parameter parameter) {
    ZonedDateTime dateValue = getValue(parameter, ZonedDateTime.now());
    return dateValue.isAfter(ZonedDateTime.parse(givenDate));
  }

  public static boolean isBefore(final String givenDate, final Parameter parameter) {
    ZonedDateTime dateValue = getValue(parameter, ZonedDateTime.now().plusYears(100));
    return dateValue.isBefore(ZonedDateTime.parse(givenDate));
  }

  private static String getValue(Parameter parameter) {
    return getValue(parameter, "null");
  }

  private static String getValue(Parameter parameter, String valueIfNull) {
    return isNull(parameter.getDataValue()) ? valueIfNull : parameter.getDataValue();
  }

  private static ZonedDateTime getValue(Parameter parameter, ZonedDateTime valueIfNull) {
    return isNull(parameter.getDataValue())
        ? valueIfNull
        : parseIsoDateTime(parameter.getDataValue());
  }
}
