// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextProvider implements ApplicationContextAware {

  private static ApplicationContext CONTEXT;

  public static ApplicationContext getApplicationContext() {
    return CONTEXT;
  }

  public static void setContext(ApplicationContext applicationContext) {
    CONTEXT = applicationContext;
  }

  public static <T> T getBean(Class<T> requiredType) {
    return CONTEXT.getBean(requiredType);
  }

  public static <T> T getBean(String beanName, Class<T> requiredType) {
    return CONTEXT.getBean(beanName, requiredType);
  }

  public static <T> T getBean(Class<T> requiredType, Object... args) {
    return CONTEXT.getBean(requiredType, args);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    setContext(applicationContext);
  }


}
