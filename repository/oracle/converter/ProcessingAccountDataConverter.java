// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.repository.oracle.converter;

import com.paysafe.mpp.commons.dto.ProcessingAccountDto;

public class ProcessingAccountDataConverter extends BaseDataConverter<ProcessingAccountDto> {

  @Override
  public Class<ProcessingAccountDto> getInstance() {
    return ProcessingAccountDto.class;
  }
}
