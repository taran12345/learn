// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.service.impl;

import com.paysafe.mastermerchant.repository.oracle.PaymentAccountDataRepository;
import com.paysafe.mastermerchant.repository.oracle.model.PaymentAccountDataEntity;
import com.paysafe.mastermerchant.service.PaymentAccountDataService;
import com.paysafe.mastermerchant.util.DataConstants;
import com.paysafe.mastermerchant.util.LogUtil;
import com.paysafe.mastermerchant.validator.MasterMerchantValidator;
import com.paysafe.mpp.commons.dto.MasterPaymentAccountDto;
import com.paysafe.mpp.commons.service.CryptoService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentAccountDataServiceImpl implements PaymentAccountDataService {

  private static final Logger logger = LoggerFactory.getLogger(PaymentAccountDataServiceImpl.class);

  @Autowired
  private CryptoService cryptoService;

  @Autowired
  private PaymentAccountDataRepository paymentDataRepository;

  /**
   * Stores payment data in oracle.
   */
  public void encryptAndSaveToOracle(MasterPaymentAccountDto paymentAccountDto) {
    logger.info("Adding paymentAccount data to oracle with id {}", paymentAccountDto.getId());

    if (!MasterMerchantValidator.isValidForStoring(paymentAccountDto)) {
      String referenceId = LogUtil.sanitizeInput(paymentAccountDto.getSourceAuthority().getReferenceId());
      logger.error("Oracle storage - invalid payment account {} ", referenceId);
      return;
    }

    MasterPaymentAccountDto encryptedDto = (MasterPaymentAccountDto) cryptoService.encrypt(paymentAccountDto);
    PaymentAccountDataEntity paymentDataEntity = PaymentAccountDataEntity.builder().createdBy(DataConstants.SYSTEM)
        .lastModifiedBy(DataConstants.SYSTEM).id(encryptedDto.getId()).data(encryptedDto).build();
    paymentDataRepository.save(paymentDataEntity);

    logger.info("Added paymentAccount data to oracle with id {}", encryptedDto.getId());
  }
}
