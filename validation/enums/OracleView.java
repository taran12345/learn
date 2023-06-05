// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.validation.enums;

public enum OracleView {
  MERCHANTS_COUNTS("getMerchantCounts"),
  MERCHANTS_GROUPED_BY_STATUS_COUNTS("getMerchantsGroupedByStatusCounts");

  private String repositoryMethodName;

  OracleView(String repositoryMethodName) {
    this.repositoryMethodName = repositoryMethodName;
  }

  public String getRepositoryMethodname() {
    return repositoryMethodName;
  }
}
