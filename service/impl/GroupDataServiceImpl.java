// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.service.impl;

import com.paysafe.mastermerchant.repository.oracle.GroupDataRepository;
import com.paysafe.mastermerchant.repository.oracle.model.GroupDataEntity;
import com.paysafe.mastermerchant.service.GroupDataService;
import com.paysafe.mastermerchant.util.DataConstants;
import com.paysafe.mastermerchant.util.LogUtil;
import com.paysafe.mastermerchant.validator.MasterMerchantValidator;
import com.paysafe.mpp.commons.dto.MasterMerchantDto;
import com.paysafe.mpp.commons.service.CryptoService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupDataServiceImpl implements GroupDataService {

  private static final Logger logger = LoggerFactory.getLogger(GroupDataServiceImpl.class);

  @Autowired
  private CryptoService cryptoService;

  @Autowired
  private GroupDataRepository groupDataRepository;

  /**
   * Stores group data in oracle.
   */
  public void encryptAndSaveToOracle(MasterMerchantDto masterMerchantDto) {
    String sanitizedId = LogUtil.sanitizeInput(masterMerchantDto.getId());
    logger.info("Adding group data to oracle with id {}", sanitizedId);

    if (!MasterMerchantValidator.isValidForStoring(masterMerchantDto)) {
      String referenceIdSanitized = LogUtil.sanitizeInput(masterMerchantDto.getSourceAuthority().getReferenceId());
      logger.error("Oracle storage - invalid group account {} ", referenceIdSanitized);
      return;
    }

    MasterMerchantDto encryptedDto = (MasterMerchantDto) cryptoService.encrypt(masterMerchantDto);
    GroupDataEntity groupDataEntity = GroupDataEntity.builder().createdBy(DataConstants.SYSTEM)
        .lastModifiedBy(DataConstants.SYSTEM).id(encryptedDto.getId()).data(encryptedDto).build();
    groupDataRepository.save(groupDataEntity);

    sanitizedId = LogUtil.sanitizeInput(encryptedDto.getId());
    logger.info("Added group data to oracle with id {}", sanitizedId);
  }
}
