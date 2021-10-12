/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine.datasources.mapper;

import java.util.List;

import com.ameriprise.utilities.rulesengine.datasources.models.DataSet;
import com.ameriprise.utilities.rulesengine.rules.models.ParameterKey;

public interface DataSetMapper {
  DataSet toDataSet(List<ParameterKey> parameterKeys, String jsonData);
}
