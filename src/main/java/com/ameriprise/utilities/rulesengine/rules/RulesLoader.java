/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine.rules;

import java.io.InputStream;

import com.ameriprise.utilities.rulesengine.rules.models.Rules;

public interface RulesLoader {
  Rules load(InputStream inputStream);

  Rules load(String path);

  Rules load(byte[] content);

  void cleanUp();
}
