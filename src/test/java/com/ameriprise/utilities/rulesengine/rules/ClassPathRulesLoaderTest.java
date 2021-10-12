/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine.rules;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ameriprise.utilities.rulesengine.rules.models.Rules;

@RunWith(JUnit4.class)
public class ClassPathRulesLoaderTest {
  ClassPathRulesLoader loader = new ClassPathRulesLoader();

  @Test
  public void testLoad() {
    Rules rules = loader.load("notification-business-rules");
    assertNotNull(rules);
  }
}
