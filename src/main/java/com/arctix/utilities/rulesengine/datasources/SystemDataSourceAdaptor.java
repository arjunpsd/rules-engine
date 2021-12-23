/* Use of this source code is subject to terms of MIT license.
 @author: Arjun Prasad
 @license: MIT
 @year: 2021 */
package com.arctix.utilities.rulesengine.datasources;

import com.arctix.utilities.rulesengine.datasources.models.DataFetchingContext;
import com.arctix.utilities.rulesengine.datasources.models.DataSet;
import com.arctix.utilities.rulesengine.rules.models.Parameter;
import com.arctix.utilities.rulesengine.rules.models.ParameterKey;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component("system-data-data-source-adaptor")
public class SystemDataSourceAdaptor implements DataSourceAdaptor {

  @Override
  public CompletableFuture<DataSet> fetch(DataFetchingContext dfe) {
    return CompletableFuture.supplyAsync(
        () ->
            new DataSet(
                dfe.getParameterKeys().stream()
                    .map(this::toParameter)
                    .collect(Collectors.toList())));
  }

  private Parameter toParameter(ParameterKey parameterKey) {
    final String attributeName = parameterKey.getName();
    if ("date-time".equalsIgnoreCase(attributeName)) {
      return new Parameter(
          parameterKey, ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    } else {
      throw new RuntimeException("system data attribute not implemented - " + attributeName);
    }
  }
}
