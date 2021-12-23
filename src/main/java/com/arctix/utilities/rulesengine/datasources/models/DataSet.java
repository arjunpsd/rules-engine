/* Use of this source code is subject to terms of MIT license.
 @author: Arjun Prasad
 @license: MIT
 @year: 2021 */
package com.arctix.utilities.rulesengine.datasources.models;

import com.arctix.utilities.rulesengine.rules.models.Parameter;
import com.arctix.utilities.rulesengine.rules.models.ParameterKey;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
