/* Use of this source code is subject to terms of MIT license.
 @author: Arjun Prasad
 @license: MIT
 @year: 2021 */
package com.arctix.utilities.rulesengine.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class FileUtils {
  public static String getJsonBody(String file) {
    StringBuilder contentBuilder = new StringBuilder();
    try (BufferedReader br =
        new BufferedReader(
            new InputStreamReader(
                Objects.requireNonNull(FileUtils.class.getResourceAsStream(file))))) {

      String sCurrentLine;
      while ((sCurrentLine = br.readLine()) != null) {
        contentBuilder.append(sCurrentLine).append("\n");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return contentBuilder.toString();
  }
}
