// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.merchantsearch.service;

import com.paysafe.mastermerchant.merchantsearch.dto.MerchantSearchRequestDto;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.sort.SortBuilder;

import java.util.List;
import java.util.Map;

public interface MerchantSearchQueryBuilderService {

  public SortBuilder buildSortQuery(MerchantSearchRequestDto requestDto);

  public BoolQueryBuilder getMerchantSearchRequestBuilder(MerchantSearchRequestDto requestDto);

  Map<String, List<TermsAggregationBuilder>> prepareAggregations(List<String> fields);
}
