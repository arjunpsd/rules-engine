/* Use of this source code is subject to terms of MIT license.
 @author: Arjun Prasad
 @license: MIT
 @year: 2021 */
package com.arctix.utilities.rulesengine.datasources.mapper;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import com.arctix.utilities.rulesengine.datasources.models.DataSet;
import com.arctix.utilities.rulesengine.rules.models.Parameter;
import com.arctix.utilities.rulesengine.rules.models.ParameterKey;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = "${dataMapping.jsonPathDataSetMapper.config.path:/json-path-config.yml}")
public class JsonPathDataSetMapper implements DataSetMapper {

  @Value("#{${jsonPaths}}")
  LinkedHashMap<String, String> jsonPaths;

  private Configuration conf;

  public JsonPathDataSetMapper() {
    conf = Configuration.defaultConfiguration();
    conf.addOptions(Option.ALWAYS_RETURN_LIST);
  }

  public DataSet toDataSet(List<ParameterKey> paramKeys, String json) {
    DocumentContext jsonDoc = JsonPath.using(conf).parse(json);
    Objects.requireNonNull(jsonPaths, "Json Paths Config cannot be null");
    Set<Parameter> data =
        paramKeys.stream()
            .flatMap(
                key -> {
                  List<String> values = jsonDoc.read(jsonPaths.get(key.toString()));
                  if (isNotEmpty(values)) {
                    return values.stream().map(value -> new Parameter(key, value));
                  }
                  return Stream.empty();
                })
            .collect(Collectors.toSet());
    return new DataSet(data);
  }
}
