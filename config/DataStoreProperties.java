// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties("data-store")
public class DataStoreProperties {
  private boolean fetchPmleMappingsFromElastic;
  private boolean fetchProcessingAccountsFromElastic;
  private boolean fetchPaymentAccountsFromElastic;

  private String readIndexName;
  private String writeIndexName;
  private String documentType;
  
  private boolean enabledRefMappingValidation;
}

