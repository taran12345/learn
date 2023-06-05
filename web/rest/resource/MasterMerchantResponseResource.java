// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.web.rest.resource;

import com.paysafe.mpp.commons.dto.MasterMerchantDto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * MasterMerchantResponseResource.java.
 *
 *
 *
 */
@Getter
@Setter
public class MasterMerchantResponseResource {

  private List<MasterMerchantDto> merchants;
  private Integer offset;
  private Integer limit;
  private Long totalSearchMatches;
}
