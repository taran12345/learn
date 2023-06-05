// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.util;

import com.paysafe.mpp.commons.dto.MasterMerchantDto;
import com.paysafe.mpp.commons.dto.MasterPaymentAccountDto;
import com.paysafe.mpp.commons.dto.ProcessingAccountDto;
import com.paysafe.mpp.commons.dto.SourceAuthority;
import com.paysafe.mpp.commons.dto.SourceAuthority.Origin;
import com.paysafe.mpp.commons.enums.EntityType;
import com.paysafe.mpp.commons.enums.MasterMerchantType;
import com.paysafe.mpp.commons.enums.PaymentAccountType;
import com.paysafe.op.errorhandling.CommonErrorCode;
import com.paysafe.op.errorhandling.exceptions.BadRequestException;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Util to work with reference mappings.
 * 
 * @author pranavrathi
 *
 */
public final class ReferenceMappingUtil {

  private static final Logger logger = LoggerFactory.getLogger(ReferenceMappingUtil.class);

  private ReferenceMappingUtil() {
  }

  /**
   * Generates all the nested level reference keys for a master merchant record.
   * 
   * @param masterMerchantRecord master merchant record
   * @return list of all the reference mappings for the nested entities
   */
  public static List<String> getAllNestedReferenceMappingKeysForMerchantRecord(MasterMerchantDto masterMerchantRecord) {
    List<String> referenceMappingKeys = new ArrayList<>();
    addReferenceMappingKeyToReferenceMappingKeysList(masterMerchantRecord.getSourceAuthority(),
        DataConstants.REFERENCE_MAPPING_NESTED_ENTITY_TYPE_GROUP, masterMerchantRecord.getType(), referenceMappingKeys);
    for (MasterPaymentAccountDto paymentAccount : CollectionUtils
        .emptyIfNull(masterMerchantRecord.getPaymentAccounts())) {
      addReferenceMappingKeyToReferenceMappingKeysList(paymentAccount.getSourceAuthority(),
          DataConstants.REFERENCE_MAPPING_NESTED_ENTITY_TYPE_PAYMENT_ACCOUNT, masterMerchantRecord.getType(),
          referenceMappingKeys);
      for (ProcessingAccountDto processingAccount : CollectionUtils
          .emptyIfNull(paymentAccount.getProcessingAccounts())) {
        addReferenceMappingKeyToReferenceMappingKeysList(processingAccount.getSourceAuthority(),
            DataConstants.REFERENCE_MAPPING_NESTED_ENTITY_TYPE_PROCESSING_ACCOUNT, masterMerchantRecord.getType(),
            referenceMappingKeys);
      }
    }
    logger.info("Reference mapping keys for merchant id {} are {}", masterMerchantRecord.getId(),
        referenceMappingKeys.toString());
    return referenceMappingKeys;
  }

  private static void addReferenceMappingKeyToReferenceMappingKeysList(SourceAuthority sourceAuthority,
      String entityType, MasterMerchantType recordType, List<String> referenceMappingKeys) {
    if (sourceAuthority != null) {
      referenceMappingKeys.add(getReferenceMappingKey(sourceAuthority.getOrigin(), entityType, recordType,
          sourceAuthority.getReferenceId()));
    }
  }

  /**
   * Generates reference mappings for the provided information.
   * 
   * @param origin origin of the record
   * @param entityType entity type inside a record
   * @param recordType record type
   * @param referenceId reference id for the entity
   * @return reference mapping key/id
   */
  public static String getReferenceMappingKey(String origin, String entityType, MasterMerchantType recordType,
      String referenceId) {
    if (recordType == null) {
      throw BadRequestException.builder().errorCode(CommonErrorCode.INVALID_FIELD)
          .detail(
              "Record type cannot be null while generating reference mapping key" + " for merchant Id " + referenceId)
          .build();
    }
    StringBuilder referenceMappingKey = new StringBuilder(origin);
    return referenceMappingKey.append(DataConstants.REFERENCE_MAPPING_KEY_SEPARATOR).append(entityType)
        .append(DataConstants.REFERENCE_MAPPING_KEY_SEPARATOR).append(recordType.toString())
        .append(DataConstants.REFERENCE_MAPPING_KEY_SEPARATOR).append(referenceId).toString();
  }

  /**
   * Generates reference mappings for the provided information.
   * {"id":"NETBANX#group#MERCHANT#1002548854","documentId":"f4ef6ea4-0eb3-4381-a9d1-266e6d33e5a8"}
   */
  public static String getReferenceMappingKey(Origin origin, EntityType entityType, PaymentAccountType accountType,
      String referenceId) {

    if (origin == null || entityType == null || accountType == null || referenceId == null) {
      throw BadRequestException.builder().errorCode(CommonErrorCode.INVALID_FIELD)
          .detail("None of the params can be NULL origin {}, entityType {}, accountType {}, referenceId {}", origin,
              entityType, accountType, referenceId)
          .build();
    }

    return new StringBuilder().append(origin.toString()).append(DataConstants.REFERENCE_MAPPING_KEY_SEPARATOR)
        .append(entityType.getlabel()).append(DataConstants.REFERENCE_MAPPING_KEY_SEPARATOR)
        .append(accountType.toString()).append(DataConstants.REFERENCE_MAPPING_KEY_SEPARATOR).append(referenceId)
        .toString();
  }
}
