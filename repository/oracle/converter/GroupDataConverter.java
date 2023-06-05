// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.repository.oracle.converter;

import com.paysafe.mpp.commons.dto.MasterMerchantDto;

public class GroupDataConverter extends BaseDataConverter<MasterMerchantDto> {

  @Override
  public Class<MasterMerchantDto> getInstance() {
    return MasterMerchantDto.class;
  }
}
