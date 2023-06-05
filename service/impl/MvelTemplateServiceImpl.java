// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.service.impl;

import com.paysafe.mastermerchant.repository.MvelTemplateEntity;
import com.paysafe.mastermerchant.repository.MvelTemplateRepository;
import com.paysafe.mastermerchant.service.MvelTemplateService;
import com.paysafe.mastermerchant.service.dto.MvelTemplateDto;
import com.paysafe.mastermerchant.service.dto.MvelTemplateResponseDto;
import com.paysafe.mastermerchant.util.DataConstants;
import com.paysafe.mastermerchant.web.rest.assembler.MvelTemplateAssembler;
import com.paysafe.op.commons.indexdb.permission.IndexDbThreadLocal;
import com.paysafe.op.errorhandling.exceptions.BadRequestException;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@ConfigurationProperties
public class MvelTemplateServiceImpl implements MvelTemplateService {

  @Autowired
  private MvelTemplateRepository mvelTemplateRepository;

  @Autowired
  private MvelTemplateAssembler mvelTemplateAssembler;

  @Override
  public MvelTemplateResponseDto createMvelTemplate(MvelTemplateDto mvelTemplateDto) {
    String currentUser = getDefaultOrAuthorizedUser();
    MvelTemplateEntity mvelTemplateEntity = mvelTemplateAssembler.convertDtoToEntity(mvelTemplateDto);
    mvelTemplateEntity.setCreatedBy(currentUser);
    mvelTemplateEntity.setLastModifiedBy(currentUser);
    mvelTemplateEntity.setCreatedDate(DateTime.now());
    mvelTemplateEntity.setLastModifiedDate(DateTime.now());
    MvelTemplateEntity createdMvelTemplateEntity = mvelTemplateRepository.save(mvelTemplateEntity);

    return mvelTemplateAssembler.convertEntityToResponseDto(createdMvelTemplateEntity);
  }

  private String getDefaultOrAuthorizedUser() {
    if (IndexDbThreadLocal.getAuthLocal() != null
        && StringUtils.isBlank(IndexDbThreadLocal.getAuthLocal().getUserName())) {
      return IndexDbThreadLocal.getAuthLocal().getUserName();
    }
    return DataConstants.ADMIN_USER;
  }

  @Override
  public MvelTemplateResponseDto getMvelTemplateByName(String templateName) {
    Optional<MvelTemplateEntity> mvelTemplateOptional =
        mvelTemplateRepository.findByTemplateName(templateName);

    if (!mvelTemplateOptional.isPresent()) {
      throw new BadRequestException.Builder().details("No template found for templateName: " + templateName).build();
    }
    return mvelTemplateAssembler.convertEntityToResponseDto(mvelTemplateOptional.get());
  }

  @Override
  public String getMvelTemplateDocByName(String templateName) {
    return getMvelTemplateByName(templateName).getTemplateDoc();
  }
}
