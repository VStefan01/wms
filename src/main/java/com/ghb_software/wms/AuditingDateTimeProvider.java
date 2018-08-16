package com.ghb_software.wms;

import org.springframework.data.auditing.DateTimeProvider;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Optional;

/**
 * Created by alexandrugheboianu on 04.06.2017.
 */
public class AuditingDateTimeProvider implements DateTimeProvider {
  @Override
  public Optional<TemporalAccessor> getNow() {
    return Optional.of(ZonedDateTime.now());
  }
}