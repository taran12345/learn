// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.service.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * GroupSearchResponseDto.java.
 * 
 * @author kamarapuprabhath
 *
 * 
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupSearchResponseDto {

  private List<GroupSearchDto> merchants; 
  private Integer offset;
  private Integer limit;
  private Long totalCount; 
}
