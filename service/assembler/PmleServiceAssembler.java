// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.service.assembler;

import com.paysafe.mastermerchant.repository.PmleEntity;
import com.paysafe.mastermerchant.service.dto.PmleDto;
import com.paysafe.mpp.commons.util.AssemblerUtils;

import org.springframework.stereotype.Component;

/**
 * Assembler dealing with to and fro conversions of {@link PmleEntity} and {@link PmleDto}.
 *
 * @author narayananulaganathan
 */
@Component
public class PmleServiceAssembler {

  /**
   * Provides {@link PmleDto} instance for a given {@link PmleEntity}.
   *
   * @param entity instance {@link PmleEntity} to be converted
   * @return instance of an equivalent {@link PmleDto}
   */
  public PmleDto dtoFromEntity(PmleEntity entity) {
    final PmleDto dto = new PmleDto();
    AssemblerUtils.copyProperties(dto, entity);
    return dto;
  }
}
