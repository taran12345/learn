// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.web.rest.resource;

import com.paysafe.mastermerchant.service.dto.GroupSearchDto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * GroupSearchResponseResource.java.
 * 
 * @author kamarapuprabhath
 *
 * 
 */
@Getter
@Setter
public class GroupSearchResponseResource {
  
  private List<GroupSearchDto> merchants; 
  private Integer offset;
  private Integer limit;
  private Long totalCount; 

}
