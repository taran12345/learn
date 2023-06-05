// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.service;

import com.paysafe.mpp.commons.dto.ProcessingAccountDto;

import java.util.List;

/**
 * ProcessingAccountService.java.
 *
 * @author satishmukku
 */

public interface ProcessingAccountService {

  /**
   * This service returns the ProcessingAccount.
   * 
   * @param processingAccountId processingAccountId
   * @return ProcessingAccountDto
   * @throws Exception exception
   */
  public ProcessingAccountDto getProcessingAccount(String processingAccountId) throws Exception;

  /**
   * This service returns the ProcessingAccount.
   * 
   * @param referenceId referenceId
   * @param source source
   * @return ProcessingAccountDto
   * @throws Exception exception
   */
  public ProcessingAccountDto getProcessingAccount(String referenceId, String source, String type)
      throws Exception;

  public List<String> getProcessingAccountIds(String pmleId) throws Exception;

}
