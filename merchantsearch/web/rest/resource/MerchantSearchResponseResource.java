// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.merchantsearch.web.rest.resource;

import com.paysafe.mpp.commons.dto.MasterMerchantDto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class MerchantSearchResponseResource {
  private List<MasterMerchantDto> merchants;
  private Map<String, List<String>> distinct;
  private Integer offset;
  private Integer limit;
  private Long totalCount;
}
