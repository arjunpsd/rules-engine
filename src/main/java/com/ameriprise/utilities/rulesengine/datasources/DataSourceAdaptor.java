/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine.datasources;

import java.util.concurrent.CompletableFuture;

import com.ameriprise.utilities.rulesengine.datasources.models.DataFetchingContext;
import com.ameriprise.utilities.rulesengine.datasources.models.DataSet;

public interface DataSourceAdaptor {
  CompletableFuture<DataSet> fetch(DataFetchingContext dfe);
}
