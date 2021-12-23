/* Use of this source code is subject to terms of MIT license.
 @author: Arjun Prasad
 @license: MIT
 @year: 2021 */
package com.arctix.utilities.rulesengine.validation;

import com.arctix.utilities.rulesengine.AbstractTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class RulesValidatorTest extends AbstractTest {

  @Autowired RulesValidator rulesValidator;

  @Test
  public void testValidateRules() throws Exception {
    rulesValidator.validateRules("notification-business-rules");
  }
}
