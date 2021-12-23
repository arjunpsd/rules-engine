/* Use of this source code is subject to terms of MIT license.
 @author: Arjun Prasad
 @license: MIT
 @year: 2021 */
package com.arctix.utilities.rulesengine.utils;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeFormat {

  private static final String ISO_DATE_TIME_FORMAT =
      "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:(\\d{2}(?:\\.\\d*)?)((-(\\d{2}):(\\d{2})|Z)?)$";
  private static final String ISO_DATE_FORMAT = "^[0-9]{4}-[0-9]{2}-[0-9]{2}$";
  private static final String UTC_ZONE_ID = "Z";

  /**
   * Parses ISO-8601 date/time string into LocalDateTime object. If provided string contains only
   * date, sets time of resulting object to start of day in UTC.
   *
   * @param dateTimeStr
   * @return
   */
  public static ZonedDateTime parseIsoDateTime(String dateTimeStr) {
    if (isBlank(dateTimeStr)) {
      return null;
    }
    try {
      if (dateTimeStr.matches(ISO_DATE_TIME_FORMAT)) {
        return ZonedDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
      } else if (dateTimeStr.matches(ISO_DATE_FORMAT)) {
        return LocalDate.parse(dateTimeStr, DateTimeFormatter.ISO_DATE)
            .atStartOfDay(ZoneId.of(UTC_ZONE_ID));
      } else {
        throw new RuntimeException("Unknown date-time format:" + dateTimeStr);
      }
    } catch (DateTimeParseException dte) {
      throw new RuntimeException("Unknown date-time format:" + dateTimeStr);
    }
  }
}
