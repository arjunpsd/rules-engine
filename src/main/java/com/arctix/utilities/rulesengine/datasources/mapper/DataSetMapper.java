/* Use of this source code is subject to terms of MIT license.
 @author: Arjun Prasad
 @license: MIT
 @year: 2021 */
package com.arctix.utilities.rulesengine.datasources.mapper;

import com.arctix.utilities.rulesengine.datasources.models.DataSet;
import com.arctix.utilities.rulesengine.rules.models.ParameterKey;
import java.util.List;

public interface DataSetMapper {
  DataSet toDataSet(List<ParameterKey> parameterKeys, String jsonData);
}
