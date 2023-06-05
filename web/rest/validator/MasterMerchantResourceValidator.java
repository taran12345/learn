// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.web.rest.validator;

import com.paysafe.mastermerchant.web.rest.resource.MasterMerchantRecordCreateRequestResource;
import com.paysafe.mpp.commons.dto.MasterMerchantDto;
import com.paysafe.mpp.commons.dto.MasterPaymentAccountDto;
import com.paysafe.mpp.commons.dto.ProcessingAccountDto;
import com.paysafe.mpp.commons.dto.SourceAuthority;
import com.paysafe.op.errorhandling.CommonErrorCode;
import com.paysafe.op.errorhandling.exceptions.BadRequestException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Master merchant resource validator.
 * 
 * @author pranavrathi
 *
 */
public final class MasterMerchantResourceValidator {
  private static final String LEVEL_DOT = " level.";

  private MasterMerchantResourceValidator() {
  }

  /**
   * Validates master merchant create request resource.
   * 
   * @param resource input resource
   */
  public static void validateMasterMerchantRecordCreateRequestResource(
      MasterMerchantRecordCreateRequestResource resource) {

    MasterMerchantDto record = resource.getRecord();
    validateMasterMerchantRecordType(record);
    validateSourceAuthority(record.getSourceAuthority(), "group");
    validatePaymentAccounts(record);
  }

  public static void validateMasterMerchantDtoUpdateRequestResource(MasterMerchantDto masterMerchantDto) {
    validateSourceAuthority(masterMerchantDto.getSourceAuthority(), "group");
    validatePaymentAccounts(masterMerchantDto);
  }

  private static void validatePaymentAccounts(MasterMerchantDto masterMerchantDto) {
    if (CollectionUtils.isEmpty(masterMerchantDto.getPaymentAccounts())) {
      throw BadRequestException.builder().errorCode(CommonErrorCode.INVALID_FIELD)
          .detail("Master merchant record must have a atleast 1 payment account information.").build();
    }
    if (masterMerchantDto.getPaymentAccounts().size() > 1) {
      throw BadRequestException.builder().errorCode(CommonErrorCode.INVALID_FIELD)
          .detail("Master merchant record must have 1 payment account information.").build();
    }
    for (MasterPaymentAccountDto paymentAccount : masterMerchantDto.getPaymentAccounts()) {
      validateSourceAuthority(paymentAccount.getSourceAuthority(), "paymentAccount");
      validateProcessingAccounts(paymentAccount);
    }
  }

  private static void validateProcessingAccounts(MasterPaymentAccountDto paymentAccount) {
    if (CollectionUtils.isEmpty(paymentAccount.getProcessingAccounts())) {
      throw BadRequestException.builder().errorCode(CommonErrorCode.INVALID_FIELD)
          .detail("All payment accounts must have atleast 1 processing account.").build();
    }
    for (ProcessingAccountDto processingAccount : paymentAccount.getProcessingAccounts()) {
      validateSourceAuthority(processingAccount.getSourceAuthority(), "processingAccount");
    }
  }

  private static void validateMasterMerchantRecordType(MasterMerchantDto record) {
    if (record.getType() == null) {
      throw BadRequestException.builder().errorCode(CommonErrorCode.INVALID_FIELD)
          .detail("Master merchant record must have a type.").build();
    }
  }

  private static void validateSourceAuthority(SourceAuthority sourceAuthority, String source) {
    if (sourceAuthority == null) {
      throw BadRequestException.builder().errorCode(CommonErrorCode.INVALID_FIELD)
          .detail("Source authority must be present at " + source + LEVEL_DOT).build();
    }
    if (StringUtils.isBlank(sourceAuthority.getLocation())) {
      throw BadRequestException.builder().errorCode(CommonErrorCode.INVALID_FIELD)
          .detail("Source authority location must be present at " + source + LEVEL_DOT).build();
    }

    if (StringUtils.isBlank(sourceAuthority.getOrigin())) {
      throw BadRequestException.builder().errorCode(CommonErrorCode.INVALID_FIELD)
          .detail("Source authority origin must be present at " + source + LEVEL_DOT).build();
    }

    if (StringUtils.isBlank(sourceAuthority.getReferenceId())) {
      throw BadRequestException.builder().errorCode(CommonErrorCode.INVALID_FIELD)
          .detail("Source authority referenceId must be present at " + source + LEVEL_DOT).build();
    }
  }
}
