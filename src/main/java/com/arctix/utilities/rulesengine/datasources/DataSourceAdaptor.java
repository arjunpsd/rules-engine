/* Use of this source code is subject to terms of MIT license.
 @author: Arjun Prasad
 @license: MIT
 @year: 2021 */
package com.arctix.utilities.rulesengine.datasources;

import com.arctix.utilities.rulesengine.datasources.models.DataFetchingContext;
import com.arctix.utilities.rulesengine.datasources.models.DataSet;
import java.util.concurrent.CompletableFuture;

public interface DataSourceAdaptor {
  CompletableFuture<DataSet> fetch(DataFetchingContext dfe);
}
