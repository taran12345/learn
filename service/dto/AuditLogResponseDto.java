// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.service.dto;

import com.paysafe.mastermerchant.dataprocessing.enums.DataSource;
import com.paysafe.mastermerchant.dataprocessing.enums.DcName;
import com.paysafe.mastermerchant.dataprocessing.enums.RunStatus;

import org.joda.time.DateTime;

import lombok.Data;

@Data
public class AuditLogResponseDto {
  private Long internalId;
  private Long id;
  private DcName dcName;
  private DataSource source;
  private RunStatus runStatus;
  private Integer successCount;
  private Integer failCount;
  private String createdBy;
  private DateTime createdDate;
  private String lastModifiedBy;
  private DateTime lastModifiedDate;
}
