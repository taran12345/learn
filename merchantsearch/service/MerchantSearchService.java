// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.merchantsearch.service;

import com.paysafe.mastermerchant.merchantsearch.dto.MerchantSearchRequestDto;
import com.paysafe.mastermerchant.merchantsearch.dto.MerchantSearchResponseDto;

import org.elasticsearch.action.search.SearchResponse;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

public interface MerchantSearchService {

  public MerchantSearchResponseDto processMerchantSearchRequest(MerchantSearchRequestDto requestDto) throws Exception;

  public MerchantSearchResponseDto getMerchantSearchResponseDto(SearchResponse searchResults, Integer offset,
      Integer limit) throws Exception;

  public void writeSearchResponseToOutputStream(List<String[]> csvData, HttpServletResponse response)
      throws IOException;

  public List<String> getReferenceIdsForEnabledMid(String mid) throws Exception;
}
