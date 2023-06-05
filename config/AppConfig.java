// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.config;

import com.paysafe.mastermerchant.fileprocessing.util.DecryptUtil;
import com.paysafe.mastermerchant.fileprocessing.util.Sftp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * Class for Bean configurations..
 * 
 * @author vamsimandalapu
 *
 */
@Configuration
public class AppConfig {

  /**
   * Lazy bean creation of Sftp.
   * 
   * @return Sftp bean
   */
  @Bean
  @Lazy
  public Sftp sftp() {
    return new Sftp();
  }

  /**
   * Lazy bean creation of DecryptUtil.
   * 
   * @return - DecryptUtil bean.
   */
  @Bean
  @Lazy
  public DecryptUtil decryptUtil() {
    return new DecryptUtil();
  }

}
