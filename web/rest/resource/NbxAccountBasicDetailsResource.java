// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.web.rest.resource;

import lombok.Data;

@Data
public class NbxAccountBasicDetailsResource {
  private String accountId;
  private String accountStatus;
  private String accountStatusModifiedDate;
  private String companyName;
  private String legalEntityCode;
  private String legalEntityDescription;
  private String pmle;
  private String mccCode;
  private String mccDescription;
  private String salesforceId;
  private String accountManager;
  private String paymentCurrency;
  private String processingCurrency;
  private String processingType;
}
