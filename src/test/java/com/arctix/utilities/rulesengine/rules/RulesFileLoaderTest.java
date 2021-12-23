/* Use of this source code is subject to terms of MIT license.
 @author: Arjun Prasad
 @license: MIT
 @year: 2021 */
package com.arctix.utilities.rulesengine.rules;

import static org.junit.Assert.assertNotNull;

import com.arctix.utilities.rulesengine.rules.models.Rules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class RulesFileLoaderTest {
  RulesFileLoader loader = new RulesFileLoader();

  @Test
  public void testLoadFromClasspath() {
    Rules rules = loader.load("classpath:/notification-business-rules.json");
    assertNotNull(rules);
  }

  @Test
  public void testLoadFromClasspath_ByName() {
    Rules rules = loader.load("notification-business-rules");
    assertNotNull(rules);
  }

  @Test
  public void testLoadFromFileSystem() {
    Rules rules = loader.load("file:./src/test/resources/notification-business-rules.json");
    assertNotNull(rules);
  }

  @Test
  public void testLoadFromByteArray() {
    byte[] content = "{\"features\": []}".getBytes();
    Rules rules = loader.load(content);
    assertNotNull(rules);
  }
}
