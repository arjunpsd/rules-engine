/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine.datasources;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ameriprise.utilities.rulesengine.AbstractTest;
import com.ameriprise.utilities.rulesengine.datasources.models.DataSet;
import com.ameriprise.utilities.rulesengine.rules.models.ParameterKey;

@RunWith(SpringJUnit4ClassRunner.class)
public class DataSourceAdaptorTest extends AbstractTest {

  @Autowired DataFetchService service;

  @Test
  public void testDataFetch() {
    // given
    Map<String, List<ParameterKey>> parametersByDataSource = new HashMap<>();

    // when
    DataSet result = service.fetchData(parametersByDataSource, null).join();

    // then
    assertNotNull(result);
  }
}
