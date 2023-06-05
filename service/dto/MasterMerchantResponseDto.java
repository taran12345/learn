// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.service.dto;

import com.paysafe.mpp.commons.dto.MasterMerchantDto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * SearchResponseDto.java.
 *
 *
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MasterMerchantResponseDto {

  private List<MasterMerchantDto> merchants;
  private Integer offset;
  private Integer limit;
  private Long totalSearchMatches;
}
