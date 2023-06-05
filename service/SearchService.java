// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.service;

import com.paysafe.mastermerchant.service.dto.SearchRequestDto;
import com.paysafe.mastermerchant.service.dto.SearchResponseDto;
import com.paysafe.mastermerchant.service.dto.TemplateMetadataDto;

/**
 * SearchService.java.
 * 
 * @author abhineetagarwal
 *
 * 
 */

public interface SearchService {

  public SearchResponseDto getRecords(SearchRequestDto searchRequest);

  public void saveTemplate(TemplateMetadataDto templateMetadata);
}
