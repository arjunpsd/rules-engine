/* Use of this source code is subject to terms of MIT license.
 @author: Arjun Prasad
 @license: MIT
 @year: 2021 */
package com.arctix.utilities.rulesengine.example.datasources;

import static com.arctix.utilities.rulesengine.utils.FileUtils.getJsonBody;

import com.arctix.utilities.rulesengine.datasources.DataSourceAdaptor;
import com.arctix.utilities.rulesengine.datasources.mapper.DataSetMapper;
import com.arctix.utilities.rulesengine.datasources.models.DataFetchingContext;
import com.arctix.utilities.rulesengine.datasources.models.DataSet;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("user-profile-data-source-adaptor")
public class UserProfileDataSourceAdaptor implements DataSourceAdaptor {

  @Autowired DataSetMapper dataSetMapper;

  @Override
  public CompletableFuture<DataSet> fetch(DataFetchingContext dfe) {

    String jsonBody = getJsonBody("/user-profile.json");
    DataSet dataSet = dataSetMapper.toDataSet(dfe.getParameterKeys(), jsonBody);
    return CompletableFuture.completedFuture(dataSet);
  }
}
