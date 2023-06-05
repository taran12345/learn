// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.util;

import static com.paysafe.mastermerchant.util.DataConstants.PAYMENT_ACCOUNTS;
import static com.paysafe.mastermerchant.util.DataConstants.PROCESSING_ACCOUNTS;

import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ElasticsearchUtil {

  /**
   *  puts the aggregation results to the map provided.
   */
  public void putResultsToMap(Map<String, List<String>> responseMap, Aggregations aggregations) {
    if (aggregations == null) {
      return;
    }
    for (String key : aggregations.asMap().keySet()) {
      if (PAYMENT_ACCOUNTS.equals(key) || PROCESSING_ACCOUNTS.equals(key)) {
        Nested nested = aggregations.get(key);
        putResultsToMap(responseMap, nested.getAggregations());
      } else {
        Terms terms = aggregations.get(key);
        responseMap.put(key,
            terms.getBuckets().stream().map(Terms.Bucket::getKeyAsString).collect(Collectors.toList()));
      }
    }
  }

}
