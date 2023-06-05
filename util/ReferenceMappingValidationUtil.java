// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.util;

import com.paysafe.mastermerchant.config.DataStoreProperties;
import com.paysafe.mastermerchant.service.ReferenceMappingDataService;
import com.paysafe.mpp.commons.dto.MasterMerchantDto;
import com.paysafe.mpp.commons.dto.SourceAuthority.Origin;
import com.paysafe.mpp.commons.enums.EntityType;
import com.paysafe.mpp.commons.enums.MasterMerchantType;
import com.paysafe.mpp.commons.enums.PaymentAccountType;
import com.paysafe.op.errorhandling.CommonErrorCode;
import com.paysafe.op.errorhandling.exceptions.BadRequestException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Validation util for reference mappings.
 * 
 * @author pranavrathi
 *
 */
@Component
public class ReferenceMappingValidationUtil {

  @Autowired
  private ReferenceMappingDataService referenceMappingDataService;

  @Autowired
  private DataStoreProperties dataStoreProperties;

  /**
   * Validates whether any of the nested reference mapping is present in the system for the given master merchant
   * record.
   * 
   * @param masterMerchantRecord master merchant record
   */
  public void validateReferenceMappingAlreadyPresent(MasterMerchantDto masterMerchantRecord) {
    if (dataStoreProperties.isEnabledRefMappingValidation()
        && isAnyNestedReferenceMappingPresent(masterMerchantRecord)) {
      throw BadRequestException.builder().errorCode(CommonErrorCode.INVALID_FIELD)
          .detail("Some nested records already present in the system.").build();
    }
  }


  /**
   * Validates if all the given attributes are not null record.
   * 
   */
  public static void validateIfAllReferenceMappingsAttributesArePresent(String origin, String entityType,
      MasterMerchantType recordType, String referenceId) {
    if (origin == null || entityType == null || recordType == null || referenceId == null) {
      throw BadRequestException.builder().errorCode(CommonErrorCode.INVALID_FIELD)
          .detail("Source authority information is not present for entity type - {} in {} record with reference id {}",
              entityType, recordType, referenceId)
          .build();
    }
  }

  private boolean isAnyNestedReferenceMappingPresent(MasterMerchantDto masterMerchantRecord) {
    String referenceId = masterMerchantRecord.getSourceAuthority().getReferenceId();
    Origin origin = Origin.valueOf(masterMerchantRecord.getSourceAuthority().getOrigin());
    PaymentAccountType accountType = PaymentAccountType.valueOf(masterMerchantRecord.getType().toString());

    String groupId = referenceMappingDataService.getUuidForReferenceMapping(origin, EntityType.GROUP,
        accountType, referenceId);
    String paymentAccountId = referenceMappingDataService.getUuidForReferenceMapping(origin,
        EntityType.PAYMENT_ACCOUNT, accountType, referenceId);
    String processingAccountId = referenceMappingDataService.getUuidForReferenceMapping(origin,
        EntityType.PROCESSING_ACCOUNT, accountType, referenceId);

    return !(groupId == null && paymentAccountId == null && processingAccountId == null);
  }
}
