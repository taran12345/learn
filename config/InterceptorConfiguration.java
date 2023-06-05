// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.config;

import com.paysafe.op.commons.indexdb.interceptor.IndexDbAuthorizationInterceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * This class stores the Interceptors configurations.
 *
 * @author arunvashisth
 *
 */
@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer {

  @Bean
  public IndexDbAuthorizationInterceptor authorizationInterceptor() {
    return new IndexDbAuthorizationInterceptor();
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(authorizationInterceptor());
  }

}
