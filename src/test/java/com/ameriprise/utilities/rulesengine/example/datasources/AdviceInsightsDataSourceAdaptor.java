/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine.example.datasources;

import static com.ameriprise.utilities.rulesengine.utils.FileUtils.getJsonBody;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ameriprise.utilities.rulesengine.datasources.DataSourceAdaptor;
import com.ameriprise.utilities.rulesengine.datasources.mapper.DataSetMapper;
import com.ameriprise.utilities.rulesengine.datasources.models.DataFetchingContext;
import com.ameriprise.utilities.rulesengine.datasources.models.DataSet;

@Component("advice-insights-data-source-adaptor")
public class AdviceInsightsDataSourceAdaptor implements DataSourceAdaptor {

  @Autowired DataSetMapper dataSetMapper;

  @Override
  public CompletableFuture<DataSet> fetch(DataFetchingContext dfe) {

    String jsonBody = getJsonBody("/advice-insights.json");
    DataSet dataSet = dataSetMapper.toDataSet(dfe.getParameterKeys(), jsonBody);
    return CompletableFuture.completedFuture(dataSet);
  }
}
