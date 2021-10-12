/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine.datasources.models;

import java.util.List;
import java.util.Map;

import com.ameriprise.utilities.rulesengine.rules.models.ParameterKey;

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
