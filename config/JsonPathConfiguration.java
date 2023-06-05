// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * JsonPathConfiguration.java.
 * 
 * @author kamarapuprabhath
 *
 * 
 */

@EnableConfigurationProperties
@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "json-path-configuration")
public class JsonPathConfiguration {
  
  private Map<String,Mapping> groupResponseMappings;

}
