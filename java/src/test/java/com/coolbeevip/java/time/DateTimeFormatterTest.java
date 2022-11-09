package com.coolbeevip.java.time;

import org.hamcrest.Matchers;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.MatcherAssert.assertThat;

public class DateTimeFormatterTest {
  private static final String PATTERN_FORMAT = "yyyy-MM-dd HH:mm:ss";

  @Test
  public void zoneUTCFormatTest() {
    // format
    LocalDateTime localDateTime = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN_FORMAT).withZone(ZoneId.from(ZoneOffset.UTC));
    String dateTimeString = formatter.format(localDateTime);

    // parse and format
    LocalDateTime localDateTime2 = LocalDateTime.from(formatter.parse(dateTimeString));
    assertThat(dateTimeString, Matchers.is(formatter.format(localDateTime2)));
  }

  @Test
  public void parseDateToTimestampLongTest() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN_FORMAT);
    LocalDateTime localDateTime = LocalDateTime.from(formatter.parse("2022-11-09 09:44:29"));
    Timestamp timestamp = Timestamp.valueOf(localDateTime);
    assertThat(timestamp.getTime(), Matchers.is(1667958269000L));
  }

  @Test
  public void timestampLongFormatTest() {
    long timestampValue = 1667958269000L;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN_FORMAT).withZone(ZoneId.systemDefault());
    Instant instant = Instant.ofEpochMilli(timestampValue);
    assertThat(formatter.format(instant), Matchers.is("2022-11-09 09:44:29"));
  }
}
