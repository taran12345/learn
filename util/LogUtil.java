// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.util;

import java.util.Objects;

public final class LogUtil {

  /**
   * Sanitizes input - removes all line breaks present if any.
   *
   * @param input input received
   * @return sanitized input
   */
  public static String sanitizeInput(String input) {
    if (Objects.isNull(input)) {
      return null;
    }
    return input.replaceAll("[\n\r\t]", "");
  }

}
