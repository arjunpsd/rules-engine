/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine.datasources.models;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.ameriprise.utilities.rulesengine.rules.models.Parameter;
import com.ameriprise.utilities.rulesengine.rules.models.ParameterKey;

public class DataSet {

  Set<Parameter> parameterSet = new HashSet<>();

  public DataSet() {}

  public DataSet(Collection<Parameter> parameters) {
    parameterSet.addAll(parameters);
  }

  public List<Parameter> getParameters(ParameterKey parameterKey) {
    return parameterSet.stream()
        .filter(parameter -> parameter.getKey().equals(parameterKey))
        .collect(Collectors.toList());
  }

  public List<Parameter> getParameters(String compositeKey) {
    return getParameters(new ParameterKey(compositeKey));
  }

  public void addParameter(Parameter parameter) {
    parameterSet.add(parameter);
  }

  public void addAll(DataSet anotherSet) {
    parameterSet.addAll(anotherSet.parameterSet);
  }
}
