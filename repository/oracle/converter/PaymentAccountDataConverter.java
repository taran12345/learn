// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.repository.oracle.converter;

import com.paysafe.mpp.commons.dto.MasterPaymentAccountDto;

public class PaymentAccountDataConverter extends BaseDataConverter<MasterPaymentAccountDto> {

  @Override
  public Class<MasterPaymentAccountDto> getInstance() {
    return MasterPaymentAccountDto.class;
  }
}
