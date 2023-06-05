// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.service;

import com.paysafe.mpp.commons.dto.MasterMerchantDto;

public interface GroupDataService {
  
  void encryptAndSaveToOracle(MasterMerchantDto masterMerchantDto);

}
