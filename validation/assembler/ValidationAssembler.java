// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.validation.assembler;

import com.paysafe.mastermerchant.util.MasterMerchantCommonUtil;
import com.paysafe.mastermerchant.validation.dto.SearchTemplateRequestDto;
import com.paysafe.mastermerchant.validation.web.rest.resource.SearchTemplateRequestResource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.elasticsearch.action.search.SearchResponse;

import java.util.Map;

public final class ValidationAssembler {

  private static final ObjectMapper mapper = new ObjectMapper();

  private ValidationAssembler() {
  }

  static {
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
  }

  /**
   * This method converts SearchTemplateRequestResource to SearchTemplateRequestDto.
   */
  public static SearchTemplateRequestDto getSearchTemplateRequestDto(SearchTemplateRequestResource inputResource) {

    SearchTemplateRequestDto searchTemplateRequestDto = new SearchTemplateRequestDto();
    searchTemplateRequestDto =
        (SearchTemplateRequestDto) MasterMerchantCommonUtil.deepCopy(inputResource, searchTemplateRequestDto);

    return searchTemplateRequestDto;
  }

  /**
   * This method converts SearchResponse to Map.
   */
  public static Map<String, Object> getSearchTemplateRequestResponse(SearchResponse searchResponse)
      throws Exception {

    return mapper.readValue(searchResponse.toString(), Map.class);
  }
}
