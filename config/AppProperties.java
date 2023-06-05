// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties("app.mtr-mcht")
public class AppProperties {

  private String fileProcessingCron;

  private boolean fileProcessingJobFlag;
  
  private boolean decryptionDeleteFlag;
  
  private String sftpHost;
  
  private String sftpUser;

  private Integer fetchDenormalizedMerchantMaxCount;
}
