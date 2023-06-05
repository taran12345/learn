// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.service.impl;

import com.paysafe.mastermerchant.repository.oracle.ReferenceMappingDataRepository;
import com.paysafe.mastermerchant.repository.oracle.model.ReferenceMappingDataEntity;
import com.paysafe.mastermerchant.service.ReferenceMappingDataService;
import com.paysafe.mastermerchant.util.DataConstants;
import com.paysafe.mastermerchant.util.LogUtil;
import com.paysafe.mastermerchant.util.ReferenceMappingUtil;
import com.paysafe.mpp.commons.dto.SourceAuthority.Origin;
import com.paysafe.mpp.commons.enums.EntityType;
import com.paysafe.mpp.commons.enums.PaymentAccountType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ReferenceMappingDataServiceImpl implements ReferenceMappingDataService {

  @Autowired
  private ReferenceMappingDataRepository referenceMappingDataRepository;

  private static final Logger logger = LoggerFactory.getLogger(ReferenceMappingDataServiceImpl.class);

  /**
   * This methods first check in oracle in referenceMapping is present if not there in oracle checks in mapr if it's.
   * there in mapr and not there in oracle it will insert mapr data in oracle if it's not there in mapr or oracle then
   * it will create new
   */
  public String getOrCreateUuidForReferenceMapping(Origin origin, EntityType entityType, PaymentAccountType accountType,
      String referenceId) {
    String referenceIdForLogging = LogUtil.sanitizeInput(referenceId);
    String uuid = getReferenceMappingUuidFromOracle(origin, entityType, accountType, referenceId);
    if (uuid != null) {
      logger.info("ReferenceMapping found in oracle for {} {}", referenceIdForLogging, origin);
      return uuid;
    }

    logger.info("ReferenceMapping found in oracle or mapr for {} {}", referenceIdForLogging, origin);
    return createReferenceMappingInOracle(origin, entityType, accountType, referenceId);
  }

  /**
   * This methods first check in oracle in referenceMapping is present if not there in oracle checks in mapr if it's.
   * there in mapr and not there in oracle it will insert mapr data in oracle.
   */
  public String getUuidForReferenceMapping(Origin origin, EntityType entityType, PaymentAccountType accountType,
      String referenceId) {
    String referenceIdForLogging = LogUtil.sanitizeInput(referenceId);
    String uuid = getReferenceMappingUuidFromOracle(origin, entityType, accountType, referenceId);
    if (uuid != null) {
      logger.info("ReferenceMapping found in oracle for {} {}", referenceIdForLogging, origin);
      return uuid;
    }

    return null;
  }

  private String getReferenceMappingUuidFromOracle(Origin origin, EntityType entityType, PaymentAccountType accountType,
      String referenceId) {
    String referenceMappingId =
        ReferenceMappingUtil.getReferenceMappingKey(origin, entityType, accountType, referenceId);

    Optional<ReferenceMappingDataEntity> referenceMappingEntity =
        referenceMappingDataRepository.findById(referenceMappingId);

    return referenceMappingEntity.map(ReferenceMappingDataEntity::getUuid).orElse(null);

  }

  private String createReferenceMappingInOracle(Origin origin, EntityType entityType, PaymentAccountType accountType,
      String referenceId) {

    String id = ReferenceMappingUtil.getReferenceMappingKey(origin, entityType, accountType, referenceId);
    String uuid = UUID.randomUUID().toString();
    ReferenceMappingDataEntity entity = ReferenceMappingDataEntity.builder().id(id).uuid(uuid)
        .createdBy(DataConstants.SYSTEM).lastModifiedBy(DataConstants.SYSTEM).build();

    ReferenceMappingDataEntity storedEntity = referenceMappingDataRepository.save(entity);

    return storedEntity.getUuid();
  }

  /**
   * This methods creates a reference mapping with given uuid data in oracle.
   */
  public String createReferenceMappingInOracle(Origin origin, EntityType entityType, PaymentAccountType accountType,
      String referenceId, String uuid) {

    String id = ReferenceMappingUtil.getReferenceMappingKey(origin, entityType, accountType, referenceId);
    ReferenceMappingDataEntity entity = ReferenceMappingDataEntity.builder().id(id).uuid(uuid)
        .createdBy(DataConstants.SYSTEM).lastModifiedBy(DataConstants.SYSTEM).build();

    ReferenceMappingDataEntity storedEntity = referenceMappingDataRepository.save(entity);

    return storedEntity.getUuid();
  }

}
