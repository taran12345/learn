// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GroupSearchRequestDto.java. This class contains fields 
 * that are returned in response of groupsearch.
 * 
 * @author kamarapuprabhath
 *
 * 
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupSearchDto {

  private String merchantId;
  private String merchantName;
  private String merchantType;
  // If there are multiple business owners present for a processing account.
  private List<String> emails;
  private String processingAccountId;

  private String paymentAccountCurrency;
  private String processingAccountCurrency;
  
  private List<String> mids; 
  private String merchantLegalEntity;
  private String paymentAccountId;
  private List<String> acquirerNames;

  //TODO:
  private String paymentAccountStatus;
  private String processingAccountStatus;
  private List<String> zip;
  private List<String> state;
  private List<String> country;
//CardType.settlementBank
  private List<String> bank;
  private String paymentAccountCreationDate;
  private String processingAccountCreationDate;

  private String partnerName;
  private String partnerId;
  private String gateway;
  private List<String> accountGroup;
  
}
