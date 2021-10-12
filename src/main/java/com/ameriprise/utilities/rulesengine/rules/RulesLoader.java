/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine.rules;

import com.ameriprise.utilities.rulesengine.rules.models.Rules;

public interface RulesLoader {
  Rules load(String path);
}
