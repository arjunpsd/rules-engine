/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine.example.datasources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import com.ameriprise.utilities.rulesengine.AbstractTest;
import com.ameriprise.utilities.rulesengine.datasources.models.DataFetchingContext;
import com.ameriprise.utilities.rulesengine.datasources.models.DataSet;
import com.ameriprise.utilities.rulesengine.rules.models.ParameterKey;

@RunWith(SpringRunner.class)
public class AdviceDataSourceAdaptorTest extends AbstractTest {

  @Autowired AdviceInsightsDataSourceAdaptor dataFetcher;

  @Test
  public void testAdviceDataFetcher() {
    // given
    List<ParameterKey> parameterKeys =
        Arrays.asList(
            new ParameterKey("advice-insights:anniversary-milestone-number"),
            new ParameterKey("advice-insights:credit-card-promotion"));
    DataFetchingContext dfe = new DataFetchingContext(parameterKeys, new HashMap<>());

    // when
    DataSet result = dataFetcher.fetch(dfe).join();

    // then
    assertNotNull(result);
    assertEquals(1, result.getParameters("advice-insights:anniversary-milestone-number").size());
  }
}
