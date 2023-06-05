// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.web.rest.assembler;

import com.paysafe.mastermerchant.repository.model.NbxMerchantAccountEntity;
import com.paysafe.mastermerchant.service.dto.MerchantAccountDto;
import com.paysafe.mastermerchant.web.rest.resource.NbxAccountBasicDetailsResource;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class NbxMerchantAccountAssembler {

  /**
   * Copies bean properties from  {@link NbxMerchantAccountEntity} to {@link MerchantAccountDto}.
   *
   * @param entity instance of {@link NbxMerchantAccountEntity}
   * @return instance of {@link MerchantAccountDto}
   */
  public MerchantAccountDto entityToDto(NbxMerchantAccountEntity entity) {
    MerchantAccountDto dto = new MerchantAccountDto();
    BeanUtils.copyProperties(entity, dto);
    return dto;
  }

  /**
   * Copies properties from {@link MerchantAccountDto} to {@link NbxAccountBasicDetailsResource}.
   *
   * @param dto instance of {@link MerchantAccountDto}
   * @return instance of {@link NbxAccountBasicDetailsResource}
   */
  public NbxAccountBasicDetailsResource dtoToResource(MerchantAccountDto dto) {
    NbxAccountBasicDetailsResource resource = new NbxAccountBasicDetailsResource();
    BeanUtils.copyProperties(dto, resource);
    return resource;
  }

}
