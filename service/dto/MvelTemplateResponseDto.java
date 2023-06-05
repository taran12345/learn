// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.service.dto;

import org.joda.time.DateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MvelTemplateResponseDto {
  private String templateName;
  private String templateDoc;
  private String createdBy;
  private DateTime createdDate;
  private String lastModifiedBy;
  private DateTime lastModifiedDate;
}
