// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.service;

import com.paysafe.mastermerchant.service.dto.MerchantAccountDto;

public interface MerchantAccountService {

  MerchantAccountDto findByAccountId(String accountId);

}
