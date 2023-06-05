// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.service.impl;

import com.paysafe.mastermerchant.repository.oracle.ProcessingAccountDataRepository;
import com.paysafe.mastermerchant.repository.oracle.model.ProcessingAccountDataEntity;
import com.paysafe.mastermerchant.service.ProcessingAccountDataService;
import com.paysafe.mastermerchant.util.DataConstants;
import com.paysafe.mastermerchant.util.LogUtil;
import com.paysafe.mastermerchant.validator.MasterMerchantValidator;
import com.paysafe.mpp.commons.dto.ProcessingAccountDto;
import com.paysafe.mpp.commons.service.CryptoService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProcessingAccountDataServiceImpl implements ProcessingAccountDataService {

  private static final Logger logger = LoggerFactory.getLogger(ProcessingAccountDataServiceImpl.class);

  @Autowired
  private CryptoService cryptoService;

  @Autowired
  private ProcessingAccountDataRepository processingDataRepository;

  /**
   * Stores processing data in oracle.
   */
  public void encryptAndSaveToOracle(ProcessingAccountDto processingAccountDto) {
    logger.info("Adding processingAccount data to oracle with id {}", processingAccountDto.getId());

    if (!MasterMerchantValidator.isValidForStoring(processingAccountDto)) {
      String referenceId = LogUtil.sanitizeInput(processingAccountDto.getSourceAuthority().getReferenceId());
      logger.error("Oracle storage - invalid processing account {} ", referenceId);
      return;
    }

    ProcessingAccountDto encryptedDto = (ProcessingAccountDto) cryptoService.encrypt(processingAccountDto);
    ProcessingAccountDataEntity processingDataEntity =
        ProcessingAccountDataEntity.builder().createdBy(DataConstants.SYSTEM).lastModifiedBy(DataConstants.SYSTEM)
            .id(encryptedDto.getId()).data(encryptedDto).build();
    processingDataRepository.save(processingDataEntity);

    logger.info("Added processingAccount data to oracle with id {}", encryptedDto.getId());
  }

  /**
   * Find single processing account by id.
   */
  @Override
  public Optional<ProcessingAccountDataEntity> findById(String processingAccountId) {
    return processingDataRepository.findById(processingAccountId);
  }

}
