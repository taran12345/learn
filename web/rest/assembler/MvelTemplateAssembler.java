// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.web.rest.assembler;

import com.paysafe.mastermerchant.repository.MvelTemplateEntity;
import com.paysafe.mastermerchant.service.dto.MvelTemplateDto;
import com.paysafe.mastermerchant.service.dto.MvelTemplateResponseDto;
import com.paysafe.mastermerchant.web.rest.resource.MvelTemplateResponse;
import com.paysafe.mpp.commons.util.AssemblerUtils;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;


@Component
public class MvelTemplateAssembler {

  /**
   * Convert MVEL template data to MVEL template entity.
   *
   * @return - MVEL entity
   */
  public MvelTemplateDto getMvelTemplateDto(String templateName, String templateDoc) {
    return MvelTemplateDto.builder().templateName(templateName).templateDoc(templateDoc).build();
  }

  /**
   * Convert MVEL entity to MVEL response POJO.
   *
   */
  public MvelTemplateResponse getMvelTemplateResponse(MvelTemplateResponseDto responseDto) {
    MvelTemplateResponse mvelTemplateResponse = new MvelTemplateResponse();
    BeanUtils.copyProperties(responseDto, mvelTemplateResponse);
    return mvelTemplateResponse;
  }

  /**
   * Convert MVEL entity file data to response.
   *
   */
  public MvelTemplateResponseDto convertEntityToResponseDto(MvelTemplateEntity mvelTemplateEntity) {
    MvelTemplateResponseDto mvelResponseDto = new MvelTemplateResponseDto();
    AssemblerUtils.copyProperties(mvelResponseDto, mvelTemplateEntity);
    return mvelResponseDto;
  }

  /**
   * This method converts MvelTemplateDto to MvelTemplateEntity.
   *
   * @param mvelTemplateDto - MVEL template DTO
   * @return mvelTemplateEntity - MVEL template Entity
   */
  public MvelTemplateEntity convertDtoToEntity(MvelTemplateDto mvelTemplateDto) {
    final MvelTemplateEntity mvelTemplateEntity = new MvelTemplateEntity();
    AssemblerUtils.copyProperties(mvelTemplateEntity, mvelTemplateDto);
    return mvelTemplateEntity;
  }
}
