// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.service;

import com.paysafe.mastermerchant.repository.oracle.model.ProcessingAccountDataEntity;
import com.paysafe.mpp.commons.dto.ProcessingAccountDto;

import java.util.Optional;

public interface ProcessingAccountDataService {
  
  void encryptAndSaveToOracle(ProcessingAccountDto processingAccountDto);

  Optional<ProcessingAccountDataEntity> findById(String processingAccountId);
}
