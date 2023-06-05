// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.web.rest.resource;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GroupSearchResource.java.
 *
 * @author kamarapuprabhath
 *
 *
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupSearchRequestResource {

  private OriginRequestResource origin;

  // deprecated
  private List<String> merchantId;
  private List<String> merchantIds;
  // deprecated
  private List<String> paymentAccountId;
  private List<String> paymentAccountIds;
  // deprecated
  private List<String> processingAccountId;
  private List<String> processingAccountIds;

  private String merchantName;
  // deprecated
  private String merchantLegalEntityId;
  private List<String> merchantLegalEntityIds;
  // deprecated
  private String merchantLegalEntity;
  private List<String> merchantLegalEntities;
  // deprecated
  private List<String> pmleId;
  private List<String> pmleIds;
  private String mid;

  private List<String> merchantType;
  private List<String> processingAccountTypes;

  private List<String> processingAccountStatus;

  private List<String> paymentAccountCurrency;
  private List<String> processingAccountCurrency;

  private LocalDateTime processingAccountCreationStartDate;
  private LocalDateTime processingAccountCreationEndDate;
  
  //searches both firstname and last name
  private String contactName;
  private List<String> contactEmails;

  private List<String> accountGroups;
  
  private String partnerName;
  // deprecated
  private String partnerId;
  private List<String> partnerIds;
  private String partnerProductName;
  private String partnerProductId;

  private String agentId;
  private String mccCode;
  private String accountManager;

  private List<String> defaultBank;
  private ThirdPartiesRequestResource thirdParties;
  private List<String> country;

  private Integer offset;
  private Integer limit;
}
