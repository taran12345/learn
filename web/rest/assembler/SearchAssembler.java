// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.web.rest.assembler;

import com.paysafe.mastermerchant.service.dto.SearchRequestDto;
import com.paysafe.mastermerchant.service.dto.SearchResponseDto;
import com.paysafe.mastermerchant.service.dto.TemplateMetadataDto;
import com.paysafe.mastermerchant.web.rest.resource.SearchRequestResource;
import com.paysafe.mastermerchant.web.rest.resource.SearchResponseResource;
import com.paysafe.mastermerchant.web.rest.resource.TemplateMetadataRequestResource;
import com.paysafe.mpp.commons.util.AssemblerUtils;

import org.springframework.stereotype.Service;

/**
 * GroupSearchAssembler.java.
 * 
 * @author kamarapuprabhath
 *
 * 
 */

@Service
public class SearchAssembler {

  /**
   * This method converts SearchRequestResource to SearchRequestDto.
   * 
   * @param searchRequestResource searchRequestResource
   * @return SearchResponseDto
   */
  public SearchRequestDto getSearchDto(SearchRequestResource searchRequestResource) {
    final SearchRequestDto searchRequestDto = new SearchRequestDto();
    AssemblerUtils.copyProperties(searchRequestDto, searchRequestResource);
    return searchRequestDto;
  }

  /**
   * This method converts SearchResponseDto to SearchResponseResource.
   * 
   * @param searchResponseDto searchResponseDto
   * @return SearchResponseResource
   */
  public SearchResponseResource getSearchResource(SearchResponseDto searchResponseDto) {
    final SearchResponseResource searchResponseResource = new SearchResponseResource();
    AssemblerUtils.copyProperties(searchResponseResource, searchResponseDto);
    return searchResponseResource;
  }

  /**
   * This method converts TemplateMetadataRequestResource to TemplateMetadataDto.
   * 
   * @param templateMetadataRequestResource templateMetadataRequestResource
   * @return TemplateMetadataDto
   */
  public TemplateMetadataDto getTemplateMetadataDto(TemplateMetadataRequestResource templateMetadataRequestResource) {
    final TemplateMetadataDto templateMetadataDto = new TemplateMetadataDto();
    AssemblerUtils.copyProperties(templateMetadataDto, templateMetadataRequestResource);
    return templateMetadataDto;
  }

}
