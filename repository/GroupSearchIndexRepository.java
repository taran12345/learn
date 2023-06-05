// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.repository;

import static com.paysafe.mastermerchant.util.DataConstants.AGGREGATION_RESULT_SIZE;
import static com.paysafe.mastermerchant.util.DataConstants.PAYMENT_ACCOUNTS;
import static com.paysafe.mastermerchant.util.DataConstants.PAYMENT_AGGREGATIONS;
import static com.paysafe.mastermerchant.util.DataConstants.PROCESSING_ACCOUNTS;
import static com.paysafe.mastermerchant.util.DataConstants.PROCESSING_AGGREGATIONS;
import static com.paysafe.mastermerchant.util.DataConstants.ROOT_AGGREGATIONS;

import com.paysafe.mastermerchant.config.DataStoreProperties;
import com.paysafe.mastermerchant.merchantsearch.dto.MerchantSearchRequestDto;
import com.paysafe.op.commons.indexdb.repository.BaseIndexRepository;
import com.paysafe.op.errorhandling.exceptions.InternalErrorException;

import org.apache.commons.collections4.MapUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * GroupSearchIndexRepository.java.
 *
 * @author kamarapuprabhath
 *
 *
 */

@Repository
public class GroupSearchIndexRepository extends BaseIndexRepository {

  private static final Logger logger = LoggerFactory.getLogger(GroupSearchIndexRepository.class);

  @Value("${search.max-size:10}")
  int size;

  private final String readIndex;

  @Autowired
  private DataStoreProperties dataStoreProperties;

  /**
   *
   * @param readIndex used for setting ES read indx.
   */
  public GroupSearchIndexRepository(@Value("${data-store.readIndexName}") String readIndex) {
    this.setIndex(readIndex);
    this.readIndex = readIndex;
  }

  /**
   * Searches groupObjects and returns matching objects.
   *
   * @param parameters used for matching records.
   *
   * @param scriptName name of the script to be used.
   * @return matchingObjects.
   */
  public SearchResponse searchScript(Map<String, Object> parameters, String scriptName) {
    return searchWithTemplate(this.readIndex, scriptName, parameters);
  }

  /**
   * This method will give aggregated merchant counts based on account status.
   * 
   * @return SearchResponse searchResponse
   */
  public SearchResponse getMerchantAggregationInfoBaseOnAccountStatus() {
    String paymentProcessingAccounts = PAYMENT_ACCOUNTS + "." + PROCESSING_ACCOUNTS;
    NestedAggregationBuilder aggregation =
        AggregationBuilders.nested(PAYMENT_ACCOUNTS, PAYMENT_ACCOUNTS)
            .subAggregation(AggregationBuilders.nested(PROCESSING_ACCOUNTS, paymentProcessingAccounts)
                .subAggregation(AggregationBuilders.terms("originLevelInfo")
                    .field(paymentProcessingAccounts + ".sourceAuthority.origin.keyword")
                    .size(AGGREGATION_RESULT_SIZE)
                    .subAggregation(AggregationBuilders.terms("accountStatusWiseCounts")
                        .field(paymentProcessingAccounts + ".status.code.keyword")
                        .size(AGGREGATION_RESULT_SIZE))));

    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.aggregation(aggregation);
    searchSourceBuilder.size(0);

    SearchRequest searchRequest = new SearchRequest();
    searchRequest.indices(this.readIndex);
    searchRequest.source(searchSourceBuilder);

    logger.debug("Elastic search query to get merchant aggregation based on accountStatus {}", searchRequest);
    return search(searchRequest);
  }

  /**
   * Searches groupObjects and returns matching objects.
   * 
   * @param sortQueryBuilder sort query builder.
   *
   * @return searchResponse.
   */
  public SearchResponse search(MerchantSearchRequestDto requestDto, BoolQueryBuilder esQuery,
      SortBuilder sortQueryBuilder, Map<String, List<TermsAggregationBuilder>> aggregationsMap) {

    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.size(requestDto.getLimit());
    searchSourceBuilder.from(requestDto.getOffset());
    searchSourceBuilder.query(esQuery);
    searchSourceBuilder.sort(sortQueryBuilder);
    searchSourceBuilder.fetchSource(requestDto.getResponseFields().stream().toArray(String[]::new), null);
    if (requestDto.getSearchAfter() != null) {
      searchSourceBuilder.searchAfter(new Object[] {requestDto.getSearchAfter()});
    }
    addAggregations(searchSourceBuilder, aggregationsMap);

    SearchRequest searchRequest = new SearchRequest();
    searchRequest.indices(dataStoreProperties.getReadIndexName());
    searchRequest.types(dataStoreProperties.getDocumentType());
    searchRequest.source(searchSourceBuilder);

    logger.info("Flexi search Query string {}", searchRequest);

    SearchResponse searchResponse;

    try {
      searchResponse = search(searchRequest);
    } catch (InternalErrorException ex) {
      logger.info("Error getting search response for request {}", requestDto, ex);
      throw ex;
    }

    return searchResponse;
  }

  private void addAggregations(
      SearchSourceBuilder searchSourceBuilder, Map<String, List<TermsAggregationBuilder>> aggregationsMap) {
    if (MapUtils.isEmpty(aggregationsMap)) {
      return;
    }
    NestedAggregationBuilder processingNestedBuilder =
        AggregationBuilders.nested(PROCESSING_ACCOUNTS, PAYMENT_ACCOUNTS + "." + PROCESSING_ACCOUNTS);
    aggregationsMap.get(PROCESSING_AGGREGATIONS).forEach(processingNestedBuilder::subAggregation);

    NestedAggregationBuilder paymentNestedBuilder = AggregationBuilders.nested(PAYMENT_ACCOUNTS, PAYMENT_ACCOUNTS);
    aggregationsMap.get(PAYMENT_AGGREGATIONS).forEach(paymentNestedBuilder::subAggregation);

    paymentNestedBuilder.subAggregation(processingNestedBuilder);

    aggregationsMap.get(ROOT_AGGREGATIONS).forEach(searchSourceBuilder::aggregation);
    searchSourceBuilder.aggregation(paymentNestedBuilder);
  }
}
