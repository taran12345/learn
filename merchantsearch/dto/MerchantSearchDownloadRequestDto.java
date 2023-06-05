// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.merchantsearch.dto;

import com.paysafe.mastermerchant.merchantsearch.enums.DownloadDateFormat;
import com.paysafe.mastermerchant.merchantsearch.enums.DownloadTimeFormat;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MerchantSearchDownloadRequestDto extends MerchantSearchRequestDto {
  private String preferredTimeZone;
  private List<String> downloadFields;
  private DownloadTimeFormat responseTimeFormat;
  private DownloadDateFormat responseDateFormat;
}
