// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.merchantsearch.enums;

public enum DownloadDateFormat {
  DD_MM_YYYY("dd-MM-yyyy"), MM_DD_YYYY("MM-dd-yyyy");

  private String dateFormat;

  private DownloadDateFormat(String dateFormat) {
    this.dateFormat = dateFormat;
  }

  public String getDateFormat() {
    return this.dateFormat;
  }
}
