/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine.validation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import com.ameriprise.utilities.rulesengine.AbstractTest;

@RunWith(SpringRunner.class)
public class RulesValidationRunnerTest extends AbstractTest {

  @Autowired RulesValidationRunner validationRunner;

  @Test
  public void testValidateRules() {
    validationRunner.validateRules("notification-business-rules");
  }
}
