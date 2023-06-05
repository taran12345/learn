// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.validation.service.impl;

import com.paysafe.mastermerchant.repository.GroupSearchIndexRepository;
import com.paysafe.mastermerchant.validation.dto.SearchTemplateRequestDto;
import com.paysafe.mastermerchant.validation.service.ElasticSearchValidationService;

import org.elasticsearch.action.search.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ElasticSearchValidationServiceImpl implements ElasticSearchValidationService {

  @Autowired
  private GroupSearchIndexRepository groupSearchIndexRepository;

  @Override
  public SearchResponse searchScript(SearchTemplateRequestDto inputDto) {
    return groupSearchIndexRepository.searchScript(inputDto.getTerms(), inputDto.getTemplateName());
  }
}
