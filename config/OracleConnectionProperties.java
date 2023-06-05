// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

import lombok.Data;

@Data
@Component
@ConfigurationProperties("oracle")
public class OracleConnectionProperties {
  private Map<String, String> connectionProperties;
}
