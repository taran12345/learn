// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.service.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class MerchantAccountDto {
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
  private String businessOwnersList;
  private String contactList;

  private String groupId;
  private String paymentAccountId;
  private String processingAccountId;
  private List<Map<String, String>> businessOwners;
}
