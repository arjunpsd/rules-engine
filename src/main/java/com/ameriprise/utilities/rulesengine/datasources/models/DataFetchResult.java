/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine.datasources.models;

import java.util.HashMap;
import java.util.Map;

public class DataFetchResult {
  private DataSet dataSet;
  private Map<String, Throwable> dataSourceWithExceptions;

  private DataFetchResult(DataSet dataSet, Map<String, Throwable> exceptions) {
    this.dataSet = dataSet;
    this.dataSourceWithExceptions = exceptions;
  }

  public DataSet getDataSet() {
    return dataSet;
  }

  public Map<String, Throwable> getDataSourceWithExceptions() {
    return dataSourceWithExceptions;
  }

  public static class Builder {
    private DataSet tempDataSet = new DataSet();
    private Map<String, Throwable> exceptions = new HashMap<>();

    public Builder addDataSet(DataSet newDataSet) {
      tempDataSet.addAll(newDataSet);
      return this;
    }

    public Builder addException(String dataSource, Throwable t) {
      exceptions.put(dataSource, t);
      return this;
    }

    public DataFetchResult build() {
      return new DataFetchResult(tempDataSet, exceptions);
    }
  }
}
