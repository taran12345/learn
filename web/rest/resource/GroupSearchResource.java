// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.web.rest.resource;

import lombok.Getter;
import lombok.Setter;

/**
 * GroupSearchResource.java.
 * 
 * @author kamarapuprabhath
 *
 * 
 */

@Getter
@Setter
public class GroupSearchResource {

  private String merchantId;
  private String merchantName;
  private String email;
  private String processingAccountId;
  private String currency;
  private String mid; 
  private String merchantLegalEntity;
  private String paymentAccountId;
  private String acquirerName;
  private String zip;
  private String state;
  private String country;
  private String bank;

  private String paymentAccountStatus;
  private String processingAccountStatus;

  private String processingAccountCreationDate;
  private String paymentAccountCreationDate;
  
//CardType.settlementBank
  
  private String partnerName;
  private String partnerId;
  private String gateway;
  private String accountGroup;

}
