// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.service;

import com.paysafe.mpp.commons.dto.MasterPaymentAccountDto;

/**
 * PaymentAccountService.java.
 *
 * @author satishmukku
 */

public interface PaymentAccountService {
  /**
   * This service returns the masterMerchantPaymentAccount.
   * 
   * @param paymentAccountId paymentAccountId
   * @return MasterPaymentAccountDTO
   * @throws Exception exception
   */
  public MasterPaymentAccountDto getPaymentAccount(String paymentAccountId) throws Exception;
}
