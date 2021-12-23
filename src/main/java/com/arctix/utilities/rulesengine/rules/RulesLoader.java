/* Use of this source code is subject to terms of MIT license.
 @author: Arjun Prasad
 @license: MIT
 @year: 2021 */
package com.arctix.utilities.rulesengine.rules;

import com.arctix.utilities.rulesengine.rules.models.Rules;
import java.io.InputStream;

public interface RulesLoader {
  Rules load(InputStream inputStream);

  Rules load(String path);

  Rules load(byte[] content);

  void cleanUp();
}
