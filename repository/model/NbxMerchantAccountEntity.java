// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.repository.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "netbanx_merchant_accounts_vw")
public class NbxMerchantAccountEntity {

  @Id
  @Column(name = "ACCOUNT_ID")
  private String accountId;

  @Column(name = "ACCOUNT_STATUS")
  private String accountStatus;

  @Column(name = "ACCOUNT_STATUS_MODIFIEDDATE")
  private String accountStatusModifiedDate;

  @Column(name = "COMPANY_NAME")
  private String companyName;

  @Column(name = "LEGALENTITY_CODE")
  private String legalEntityCode;

  @Column(name = "LEGALENTITY_DESC")
  private String legalEntityDescription;

  @Column(name = "PMLE")
  private String pmle;

  @Column(name = "MCC_CODE")
  private String mccCode;

  @Column(name = "MCC_DESCRIPTION")
  private String mccDescription;

  @Column(name = "SALESFORCEID")
  private String salesforceId;

  @Column(name = "ACCOUNT_MANAGER")
  private String accountManager;

  @Column(name = "PAYMENT_CURRENCY")
  private String paymentCurrency;

  @Column(name = "PROCESSING_CURRENCY")
  private String processingCurrency;

  @Column(name = "PROCESSING_TYPE")
  private String processingType;
}
