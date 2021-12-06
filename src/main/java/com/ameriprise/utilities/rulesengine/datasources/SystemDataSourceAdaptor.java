/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine.datasources;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ameriprise.utilities.rulesengine.datasources.models.DataFetchingContext;
import com.ameriprise.utilities.rulesengine.datasources.models.DataSet;
import com.ameriprise.utilities.rulesengine.rules.models.Parameter;
import com.ameriprise.utilities.rulesengine.rules.models.ParameterKey;

@Component("system-data-data-source-adaptor")
public class SystemDataSourceAdaptor implements DataSourceAdaptor {

  @Override
  public CompletableFuture<DataSet> fetch(DataFetchingContext dfe) {
    return CompletableFuture.supplyAsync(
        () ->
            new DataSet(
                dfe.getParameterKeys().stream()
                    .map(this::toParameter)
                    .collect(Collectors.toList())));
  }

  private Parameter toParameter(ParameterKey parameterKey) {
    final String attributeName = parameterKey.getName();
    if ("date-time".equalsIgnoreCase(attributeName)) {
      return new Parameter(
          parameterKey, ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    } else {
      throw new RuntimeException("system data attribute not implemented - " + attributeName);
    }
  }
}
