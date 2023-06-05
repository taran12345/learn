// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.web.rest.assembler;

import com.paysafe.mastermerchant.service.dto.PmleDto;
import com.paysafe.mastermerchant.web.rest.resource.PmleResponse;
import com.paysafe.mpp.commons.util.AssemblerUtils;

import org.springframework.stereotype.Component;

/**
 * Assembler dealing with to and fro conversions of {@link PmleResponse} and {@link PmleDto}.
 *
 * @author narayananulaganathan
 */
@Component
public class PmleAssembler {

  /**
   * Provides {@link PmleResponse} instance from the given {@link PmleDto}.
   *
   * @param dto instance of {@link PmleDto} to be converted
   * @return instance of an equivalent {@link PmleResponse}
   */
  public PmleResponse responseFromDto(PmleDto dto) {
    final PmleResponse response = new PmleResponse();
    AssemblerUtils.copyProperties(response, dto);
    return response;
  }
}
