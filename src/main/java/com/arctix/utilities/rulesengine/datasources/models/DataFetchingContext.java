/* Use of this source code is subject to terms of MIT license.
 @author: Arjun Prasad
 @license: MIT
 @year: 2021 */
package com.arctix.utilities.rulesengine.datasources.models;

import com.arctix.utilities.rulesengine.rules.models.ParameterKey;
import java.util.List;
import java.util.Map;

public class DataFetchingContext {

  List<ParameterKey> parameterKeys;
  Map<?, ?> userData;

  public DataFetchingContext(List<ParameterKey> parameterKeys, Map<?, ?> userData) {
    this.parameterKeys = parameterKeys;
    this.userData = userData;
  }

  public List<ParameterKey> getParameterKeys() {
    return parameterKeys;
  }

  public void setParameterKeys(List<ParameterKey> parameterKeys) {
    this.parameterKeys = parameterKeys;
  }

  public Map<?, ?> getUserData() {
    return userData;
  }

  public void setUserData(Map<?, ?> userData) {
    this.userData = userData;
  }
}
