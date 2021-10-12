/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine.datasources;

import static java.util.Objects.isNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.ameriprise.utilities.rulesengine.datasources.models.DataFetchingContext;
import com.ameriprise.utilities.rulesengine.datasources.models.DataSet;
import com.ameriprise.utilities.rulesengine.rules.models.ParameterKey;

@Component
@Primary
public class DataFetchService {

  @Autowired ApplicationContext appContext;

  public CompletableFuture<DataSet> fetchData(
      final Map<String, List<ParameterKey>> parametersByDataSource, final Map<?, ?> userData) {

    if (isNull(parametersByDataSource) || parametersByDataSource.isEmpty()) {
      return CompletableFuture.completedFuture(new DataSet());
    }

    CompletableFuture<?>[] dataStream =
        parametersByDataSource.entrySet().stream()
            .map(entry -> fetchDataFromDataSource(entry.getKey(), entry.getValue(), userData))
            .toArray(CompletableFuture<?>[]::new);

    return CompletableFuture.allOf(dataStream)
        .thenApply(
            unused ->
                Arrays.stream(dataStream).map(CompletableFuture::join).collect(Collectors.toList()))
        .thenApply(this::toDataSet);
  }

  protected DataSet toDataSet(List<?> objects) {
    return objects.stream()
        .map(o -> (DataSet) o)
        .collect(DataSet::new, DataSet::addAll, (dataSet, dataSet2) -> {});
  }

  protected CompletableFuture<DataSet> fetchDataFromDataSource(
      String dataSource, List<ParameterKey> parameterKeys, Map<?, ?> userData) {
    DataSourceAdaptor dataSourceAdaptor = getDataFetcher(dataSource);
    return dataSourceAdaptor.fetch(new DataFetchingContext(parameterKeys, userData));
  }

  protected DataSourceAdaptor getDataFetcher(String name) {
    return (DataSourceAdaptor) appContext.getBean(name);
  }
}
