/* Use of this source code is subject to terms of MIT license.
 @author: Arjun Prasad
 @license: MIT
 @year: 2021 */
package com.arctix.utilities.rulesengine.example.datasources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.arctix.utilities.rulesengine.AbstractTest;
import com.arctix.utilities.rulesengine.datasources.models.DataFetchingContext;
import com.arctix.utilities.rulesengine.datasources.models.DataSet;
import com.arctix.utilities.rulesengine.rules.models.ParameterKey;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class UserProfileDataSourceAdaptorTest extends AbstractTest {

  @Autowired UserProfileDataSourceAdaptor dataSourceAdaptor;

  @Test
  public void testFetchRegistrationData() {
    // given
    List<ParameterKey> parameterKeys =
        Arrays.asList(new ParameterKey("user-profile:registration-date"));
    DataFetchingContext dfe = new DataFetchingContext(parameterKeys, new HashMap<>());

    // when
    DataSet result = dataSourceAdaptor.fetch(dfe).join();

    // then
    assertNotNull(result);
    assertEquals(1, result.getParameters("user-profile:registration-date").size());
  }
}
