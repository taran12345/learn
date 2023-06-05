// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.service;

import com.paysafe.mastermerchant.service.dto.MvelTemplateDto;
import com.paysafe.mastermerchant.service.dto.MvelTemplateResponseDto;

public interface MvelTemplateService {
  public MvelTemplateResponseDto createMvelTemplate(MvelTemplateDto mvelTemplateDto);

  public MvelTemplateResponseDto getMvelTemplateByName(String templateName);
  
  public String getMvelTemplateDocByName(String templateName);
}
