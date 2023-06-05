// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.service.dto;

import com.paysafe.mastermerchant.dataprocessing.enums.DataSource;
import com.paysafe.mastermerchant.dataprocessing.enums.TemplateType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MvelTemplateSearchCriteriaDto {

  private Long id;

  private String templateName;

  private TemplateType templateType;

  private DataSource dataSource;
}
