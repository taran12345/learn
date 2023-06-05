// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties("spring-scheduler")
public class SpringSchedulerConfig {
  private String serviceId;
  private boolean enabled;
  private String nodeIdentifierMethodName;
}
