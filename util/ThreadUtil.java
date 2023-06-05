// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * ThreadUtil.java.
 * 
 * @author arunvashisth
 * 
 */
public final class ThreadUtil {
  
  private static final Logger logger = LoggerFactory.getLogger(ThreadUtil.class);

  private static ThreadLocal<Map<String, Object>> masterMerchantThreadContext =
      new ThreadLocal<Map<String, Object>>() {
        @Override
        protected Map<String, Object> initialValue() {
          return new HashMap<>();
        }
      };

  private ThreadUtil() {

  }

  /**
   * @return Map.
   */
  public static Map<String, Object> getMasterMerchantThreadContext() {
    return masterMerchantThreadContext.get();
  }

  /**
   * add ContextMap to threadLocal.
   */
  public static void addContextMapToThreadLocal(Map<String, Object> requestAttributes) {
    if (requestAttributes == null) {
      logger.info("requestAttributes is empty.");
    } else {
      masterMerchantThreadContext.set(requestAttributes);
    }
  }

  /**
   * remove ContextMap from threadLocal.
   */
  public static void unloadContextMapToThreadLocal() {
    masterMerchantThreadContext.remove();
  }
}
