/* Use of this source code is subject to terms of MIT license.
 @author: Arjun Prasad
 @license: MIT
 @year: 2021 */
package com.arctix.utilities.rulesengine.datasources.mapper;

import static org.junit.Assert.assertNotNull;

import com.arctix.utilities.rulesengine.AbstractTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class JsonPathDataSetMapperTest extends AbstractTest {

  @Autowired JsonPathDataSetMapper dataSetMapper;

  @Test
  public void testToDataSet() {

    assertNotNull(dataSetMapper);
    assertNotNull(dataSetMapper.jsonPaths);
  }
}
