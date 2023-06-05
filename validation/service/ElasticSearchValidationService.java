// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.validation.service;

import com.paysafe.mastermerchant.validation.dto.SearchTemplateRequestDto;

import org.elasticsearch.action.search.SearchResponse;

public interface ElasticSearchValidationService {

  public SearchResponse searchScript(SearchTemplateRequestDto inputDto);
}
