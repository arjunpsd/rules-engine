/* Use of this source code is subject to terms of MIT license.
 @author: Arjun Prasad
 @license: MIT
 @year: 2021 */
package com.arctix.utilities.rulesengine.datasources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.arctix.utilities.rulesengine.AbstractTest;
import com.arctix.utilities.rulesengine.datasources.models.DataFetchResult;
import com.arctix.utilities.rulesengine.datasources.models.DataFetchingContext;
import com.arctix.utilities.rulesengine.datasources.models.DataSet;
import com.arctix.utilities.rulesengine.rules.models.ParameterKey;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class DataFetchServiceTest extends AbstractTest {

  @Autowired DataFetchService service;

  @Test
  public void testDataFetch_EmptyParams() {
    // given
    Map<String, List<ParameterKey>> parametersByDataSource = new HashMap<>();

    // when
    DataFetchResult result = service.fetchData(parametersByDataSource, null).join();

    // then
    assertNotNull(result);
  }

  @Test
  public void testDataFetch_Success() {
    // given
    Map<String, List<ParameterKey>> parametersByDataSource = getMockedParams();

    // when
    DataFetchResult result = service.fetchData(parametersByDataSource, null).join();

    // then
    assertNotNull(result);
    assertEquals(
        1,
        result
            .getDataSet()
            .getParameters(new ParameterKey("user-profile:registration-date"))
            .size());
    assertEquals(0, result.getDataSourceWithExceptions().size());
  }

  @Test
  public void testDataFetch_AdaptorNotExists() {
    // given
    Map<String, List<ParameterKey>> parametersByDataSource = getMockedParams();
    parametersByDataSource.put(
        "fake-data-source", Arrays.asList(new ParameterKey("fake-data-source", "exception")));

    // when
    DataFetchResult result = service.fetchData(parametersByDataSource, null).join();

    // then
    assertNotNull(result);
    assertEquals(
        1,
        result
            .getDataSet()
            .getParameters(new ParameterKey("user-profile:registration-date"))
            .size());
    assertEquals(1, result.getDataSourceWithExceptions().size());
  }

  @Test
  public void testDataFetch_AdaptorThrowsException() {
    // given
    Map<String, List<ParameterKey>> parametersByDataSource = getMockedParams();
    parametersByDataSource.put(
        "exceptional", Arrays.asList(new ParameterKey("fake-data-source", "exception")));

    // when
    DataFetchResult result = service.fetchData(parametersByDataSource, null).join();

    // then
    assertNotNull(result);
    assertEquals(
        1,
        result
            .getDataSet()
            .getParameters(new ParameterKey("user-profile:registration-date"))
            .size());
    assertEquals(1, result.getDataSourceWithExceptions().size());
  }

  private Map<String, List<ParameterKey>> getMockedParams() {
    Map<String, List<ParameterKey>> paramMap = new HashMap<>();
    paramMap.put("user-profile", Arrays.asList(new ParameterKey("user-profile:registration-date")));
    paramMap.put("user-profile", Arrays.asList(new ParameterKey("user-profile:registration-date")));
    return paramMap;
  }

  @Component("exceptional-data-source-adaptor")
  public static class ExceptionDataAdaptor implements DataSourceAdaptor {

    @Override
    public CompletableFuture<DataSet> fetch(DataFetchingContext dfe) {
      return CompletableFuture.supplyAsync(
          () -> {
            throw new CompletionException(new IllegalArgumentException("Data Source Error"));
          });
    }
  }
}
