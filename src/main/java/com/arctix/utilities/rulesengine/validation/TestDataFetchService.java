/* Use of this source code is subject to terms of MIT license.
 @author: Arjun Prasad
 @license: MIT
 @year: 2021 */
package com.arctix.utilities.rulesengine.validation;

import com.arctix.utilities.rulesengine.datasources.DataFetchService;
import com.arctix.utilities.rulesengine.datasources.DataSourceAdaptor;
import com.arctix.utilities.rulesengine.datasources.models.DataFetchingContext;
import com.arctix.utilities.rulesengine.datasources.models.DataSet;
import com.arctix.utilities.rulesengine.rules.models.Parameter;
import com.arctix.utilities.rulesengine.rules.models.Rules;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Component;

/** This is a mock data source adaptor to be used when executing business rules during testing. */
@Component
public class TestDataFetchService extends DataFetchService implements DataSourceAdaptor {

  /**
   * Override any data fetcher in the rules with this mock that fetches test-data from test case
   * itself
   *
   * @param name
   * @return
   */
  @Override
  protected DataSourceAdaptor getDataFetcher(String name) {
    return this;
  }

  /**
   * Injects test data from test cases when evaluating rules using the rules engine. Test data is
   * available within `userData` of the data fetching context. (injected by the test runner)
   *
   * @param dfe
   * @return
   */
  @Override
  public CompletableFuture<DataSet> fetch(DataFetchingContext dfe) {
    Rules tests = (Rules) dfe.getUserData().get("tests");
    List<Parameter> testData = tests.getFeatures().get(0).getTestData();
    return CompletableFuture.supplyAsync(
        () -> testData.stream().collect(DataSet::new, DataSet::addParameter, (d1, d2) -> {}));
  }
}
