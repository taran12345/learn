// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.merchantsearch.enums;

public enum DownloadTimeFormat {
  HR_24("HH:mm:ss"), HR_12("hh:mm:ss aa");

  private String timeFormat;

  DownloadTimeFormat(String timeFormat) {
    this.timeFormat = timeFormat;
  }

  public String getTimeFormat() {
    return this.timeFormat;
  }
}
