// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.validation.dto;

import java.util.Map;

import lombok.Data;

@Data
public class SearchTemplateRequestDto {
  private String templateName;

  private Map<String, Object> terms;
}
