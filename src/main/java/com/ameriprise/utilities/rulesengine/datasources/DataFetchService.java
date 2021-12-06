/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine.datasources;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.ameriprise.utilities.rulesengine.datasources.models.DataFetchResult;
import com.ameriprise.utilities.rulesengine.datasources.models.DataFetchingContext;
import com.ameriprise.utilities.rulesengine.datasources.models.DataSet;
import com.ameriprise.utilities.rulesengine.rules.models.ParameterKey;

@Component
@Primary
public class DataFetchService {

  @Autowired ApplicationContext appContext;

  private static final String ADAPTOR_SUFFIX = "-data-source-adaptor";

  private static final Logger LOG = LoggerFactory.getLogger(DataFetchService.class);

  public CompletableFuture<DataFetchResult> fetchData(
      final Map<String, List<ParameterKey>> parametersByDataSource, final Map<?, ?> userData) {

    if (isNull(parametersByDataSource) || parametersByDataSource.isEmpty()) {
      return CompletableFuture.completedFuture(new DataFetchResult.Builder().build());
    }

    CompletableFuture<?>[] dataStream =
        parametersByDataSource.entrySet().stream()
            .map(entry -> fetchDataFromDataSource(entry.getKey(), entry.getValue(), userData))
            .toArray(CompletableFuture<?>[]::new);

    return CompletableFuture.allOf(dataStream)
        .thenApply(
            unused ->
                Arrays.stream(dataStream).map(CompletableFuture::join).collect(Collectors.toList()))
        .thenApply(this::toDataFetchResult);
  }

  protected DataFetchResult toDataFetchResult(List<?> objects) {
    return objects.stream()
        .map(o -> (FetchResult) o)
        .collect(
            DataFetchResult.Builder::new,
            (builder, result) -> {
              if (result.hasException()) {
                builder.addException(result.getDataSourceName(), result.getException());
              } else {
                builder.addDataSet(result.getDataSet());
              }
            },
            (dataSet, dataSet2) -> {})
        .build();
  }

  protected CompletableFuture<FetchResult> fetchDataFromDataSource(
      String dataSource, List<ParameterKey> parameterKeys, Map<?, ?> userData) {
    try {
      DataSourceAdaptor dataSourceAdaptor = getDataFetcher(dataSource);
      return dataSourceAdaptor
          .fetch(new DataFetchingContext(parameterKeys, userData))
          .thenApply(dataSet -> new FetchResult(dataSource, dataSet))
          .exceptionally(
              throwable -> {
                LOG.error("Exception fetching data from data source {}", dataSource, throwable);
                return new FetchResult(dataSource, throwable);
              });
    } catch (Exception any) {
      LOG.error("Exception fetching data from data source {}", dataSource, any);
      return CompletableFuture.completedFuture(new FetchResult(dataSource, any));
    }
  }

  protected DataSourceAdaptor getDataFetcher(String name) {
    return (DataSourceAdaptor) appContext.getBean(name + ADAPTOR_SUFFIX);
  }

  /** Represents the result of data fetch from a data source adapter. */
  private static class FetchResult {
    String dataSourceName;
    DataSet dataSet;
    Throwable exception;

    public FetchResult(String dataSourceName, DataSet dataSet) {
      this.dataSourceName = dataSourceName;
      this.dataSet = dataSet;
    }

    public FetchResult(String dataSourceName, Throwable exception) {
      this.dataSourceName = dataSourceName;
      this.exception = exception;
    }

    public String getDataSourceName() {
      return dataSourceName;
    }

    public DataSet getDataSet() {
      return dataSet;
    }

    public Throwable getException() {
      return exception;
    }

    public boolean hasException() {
      return nonNull(exception);
    }
  }
}
