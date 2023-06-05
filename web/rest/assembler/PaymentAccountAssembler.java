// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.web.rest.assembler;

import com.paysafe.mpp.commons.dto.MasterPaymentAccountDto;
import com.paysafe.mpp.commons.mastermerchant.resources.PaymentAccountResource;
import com.paysafe.mpp.commons.util.AssemblerUtils;

import org.springframework.stereotype.Component;

/**
 * This is an assembler class for PaymentAccountService.
 * 
 * @author satishmukku
 *
 */
@Component
public class PaymentAccountAssembler {

  /**
   * This method converts paymentAccount to MasterMerchantResource.
   * 
   * @param paymentAccount
   *          paymentAccountDto
   * @return MasterPaymentAccountResource
   */

  public PaymentAccountResource
      getPaymentAccountResource(MasterPaymentAccountDto paymentAccount) {
    final PaymentAccountResource paymentAccountResource = new PaymentAccountResource();
    AssemblerUtils.copyProperties(paymentAccountResource, paymentAccount);
    return paymentAccountResource;
  }
}
