// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.validator;

import com.paysafe.mpp.commons.dto.MasterMerchantDto;
import com.paysafe.mpp.commons.dto.MasterPaymentAccountDto;
import com.paysafe.mpp.commons.dto.ProcessingAccountDto;
import com.paysafe.mpp.commons.dto.SourceAuthority;

public final class MasterMerchantValidator {

  /**
   * Validates given masterMerchantDto.
   * 
   * @param masterMerchantDto masterMerchantDto
   */
  public static Boolean isValid(MasterMerchantDto masterMerchantDto) {
    boolean isGroupValid = isValidForStoring(masterMerchantDto);
    boolean isPaymentValid = isValidForStoring(masterMerchantDto.getPaymentAccounts().get(0));
    boolean isProcessingValid =
        isValidForStoring(masterMerchantDto.getPaymentAccounts().get(0).getProcessingAccounts().get(0));

    return isGroupValid && isPaymentValid && isProcessingValid;
  }

  public static Boolean isValidForStoring(MasterMerchantDto masterMerchantDto) {
    return hasValidSourceAuthority(masterMerchantDto.getSourceAuthority()) && masterMerchantDto.getId() != null
        && masterMerchantDto.getType() != null;
  }

  public static Boolean isValidForStoring(MasterPaymentAccountDto paymentAccountDto) {
    return hasValidSourceAuthority(paymentAccountDto.getSourceAuthority()) && paymentAccountDto.getId() != null;
  }

  public static Boolean isValidForStoring(ProcessingAccountDto processingAccountDto) {
    return hasValidSourceAuthority(processingAccountDto.getSourceAuthority()) && processingAccountDto.getId() != null;
  }

  public static Boolean hasValidSourceAuthority(SourceAuthority sourceAuthority) {
    return sourceAuthority != null && sourceAuthority.getLocation() != null && sourceAuthority.getOrigin() != null
        && sourceAuthority.getReferenceId() != null;
  }
}
