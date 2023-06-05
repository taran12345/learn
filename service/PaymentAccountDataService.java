// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.service;

import com.paysafe.mpp.commons.dto.MasterPaymentAccountDto;

public interface PaymentAccountDataService {

  void encryptAndSaveToOracle(MasterPaymentAccountDto paymentAccountDto);
}
