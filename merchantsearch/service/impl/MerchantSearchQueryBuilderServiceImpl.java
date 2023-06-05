// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.merchantsearch.service.impl;

import static com.paysafe.mastermerchant.util.DataConstants.AGGREGATION_RESULT_SIZE;
import static com.paysafe.mastermerchant.util.DataConstants.PAYMENT_ACCOUNTS;
import static com.paysafe.mastermerchant.util.DataConstants.PAYMENT_AGGREGATIONS;
import static com.paysafe.mastermerchant.util.DataConstants.PROCESSING_ACCOUNTS;
import static com.paysafe.mastermerchant.util.DataConstants.PROCESSING_AGGREGATIONS;
import static com.paysafe.mastermerchant.util.DataConstants.ROOT_AGGREGATIONS;

import com.paysafe.mastermerchant.merchantsearch.config.ElasticSearchMetaDataConfig;
import com.paysafe.mastermerchant.merchantsearch.dto.MerchantSearchRequestDto;
import com.paysafe.mastermerchant.merchantsearch.dto.MerchantSearchRequestDto.RangeParamMinMax;
import com.paysafe.mastermerchant.merchantsearch.enums.SearchOperator;
import com.paysafe.mastermerchant.merchantsearch.service.MerchantSearchQueryBuilderService;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class MerchantSearchQueryBuilderServiceImpl implements MerchantSearchQueryBuilderService {

  @Autowired
  private ElasticSearchMetaDataConfig elasticSearchMetaDataConfig;

  /**
   * builds sort query.
   * 
   */
  public SortBuilder buildSortQuery(MerchantSearchRequestDto requestDto) {
    Boolean isNested = elasticSearchMetaDataConfig.getSortMapping().get(requestDto.getSortField()).getIsNested();
    String sortFieldSearchPath =
        elasticSearchMetaDataConfig.getSortMapping().get(requestDto.getSortField()).getSearchPath();
    String sortFieldNestedPath =
        elasticSearchMetaDataConfig.getSortMapping().get(requestDto.getSortField()).getNestedPath();

    if (isNested) {
      return SortBuilders.fieldSort(sortFieldSearchPath).order(requestDto.getSortOrder())
          .setNestedPath(sortFieldNestedPath);
    } else {
      return SortBuilders.fieldSort(sortFieldSearchPath).order(requestDto.getSortOrder());
    }
  }

  /**
   * builds bool query.
   * 
   */
  public BoolQueryBuilder getMerchantSearchRequestBuilder(MerchantSearchRequestDto requestDto) {
    BoolQueryBuilder query = QueryBuilders.boolQuery();

    buildFilterQuery(query, requestDto);
    buildSearchQuery(query, requestDto);
    buildMultiMatchQuery(query, requestDto);
    buildExistsQuery(query, requestDto);
    buildRangeQuery(query, requestDto);

    return query;
  }

  /**
   * prepares aggregations for provided fields.
   */
  @Override
  public Map<String, List<TermsAggregationBuilder>> prepareAggregations(List<String> fields) {
    if (CollectionUtils.isEmpty(fields)) {
      return Collections.emptyMap();
    }
    fields = fields.stream().distinct().collect(Collectors.toList());
    Map<String, List<TermsAggregationBuilder>> map = new HashMap<>();
    map.put(ROOT_AGGREGATIONS, getAggregationsByLevel(fields, field -> !isSearchNestedField(field), field -> true));
    map.put(PAYMENT_AGGREGATIONS, getAggregationsByLevel(fields, this::isSearchNestedField,
        field -> PAYMENT_ACCOUNTS.equals(getSearchNestedPath(field))));
    map.put(PROCESSING_AGGREGATIONS, getAggregationsByLevel(fields, this::isSearchNestedField,
        field -> (PAYMENT_ACCOUNTS + "." + PROCESSING_ACCOUNTS).equals(getSearchNestedPath(field))));

    return map;
  }

  private TermsAggregationBuilder prepareTermsAggregationBuilder(String field) {
    return AggregationBuilders.terms(field).size(AGGREGATION_RESULT_SIZE).field(getSearchPath(field));
  }

  private List<TermsAggregationBuilder> getAggregationsByLevel(
      List<String> fields, Predicate<String> filterPredicate, Predicate<String> equalityPredicate) {
    return fields.stream().filter(filterPredicate).filter(equalityPredicate)
        .map(this::prepareTermsAggregationBuilder).collect(Collectors.toList());
  }

  private boolean isSearchNestedField(String key) {
    return BooleanUtils.isTrue(elasticSearchMetaDataConfig.getSearchMapping().get(key).getIsNested());
  }

  private boolean isRangeNestedField(String key) {
    return BooleanUtils.isTrue(elasticSearchMetaDataConfig.getRangeMapping().get(key).getIsNested());
  }

  private String getSearchNestedPath(String key) {
    return elasticSearchMetaDataConfig.getSearchMapping().get(key).getNestedPath();
  }

  private String getRangeNestedPath(String key) {
    return elasticSearchMetaDataConfig.getRangeMapping().get(key).getNestedPath();
  }

  private String getSearchPath(String key) {
    return elasticSearchMetaDataConfig.getSearchMapping().get(key).getSearchPath();
  }

  private String getRangeSearchPath(String key) {
    return elasticSearchMetaDataConfig.getRangeMapping().get(key).getSearchPath();
  }

  private void buildFilterQuery(BoolQueryBuilder query, MerchantSearchRequestDto requestDto) {
    if (requestDto.getOperator() == SearchOperator.AND) {

      requestDto.getFilterParams().forEach((key, value) -> {
        if (isSearchNestedField(key)) {
          query.filter(getNestedFilterTermsQuery(key, value));
        } else {
          query.filter(QueryBuilders.termsQuery(getSearchPath(key), value));
        }
      });

    } else {

      requestDto.getFilterParams().forEach((key, value) -> {
        if (isSearchNestedField(key)) {
          query.should(getNestedShouldTermsQuery(key, value));
        } else {
          query.should(QueryBuilders.termsQuery(getSearchPath(key), value));
        }
      });

    }
  }

  private void buildSearchQuery(BoolQueryBuilder query, MerchantSearchRequestDto requestDto) {
    if (requestDto.getOperator() == SearchOperator.AND) {

      requestDto.getSearchParams().forEach((key, value) -> {
        if (isSearchNestedField(key)) {
          query.filter(getNestedFilterWildCardQuery(key, value));
        } else {
          query.filter(QueryBuilders.wildcardQuery(getSearchPath(key), value));
        }
      });

    } else {

      requestDto.getSearchParams().forEach((key, value) -> {
        if (isSearchNestedField(key)) {
          query.should(getNestedShouldWildCardQuery(key, value));
        } else {
          query.should(QueryBuilders.wildcardQuery(getSearchPath(key), value));
        }
      });

    }
  }

  private void buildMultiMatchQuery(BoolQueryBuilder query, MerchantSearchRequestDto requestDto) {

    if (requestDto.getOperator() == SearchOperator.AND) {

      requestDto.getMultiMatchParams().forEach((value, searchFields) -> {
        List<String> searchFieldsWithSearchPath = new ArrayList<>();

        for (String searchField : searchFields) {
          searchFieldsWithSearchPath.add(getSearchPath(searchField));
        }

        if (isSearchNestedField(searchFields.get(0))) {
          query.filter(getNestedFilterMultiMatchQuery(getSearchNestedPath(searchFields.get(0)), value,
              searchFieldsWithSearchPath));
        } else {
          query.filter(QueryBuilders.multiMatchQuery(value,
              searchFieldsWithSearchPath.toArray(new String[searchFieldsWithSearchPath.size()])));
        }
      });
      
    } else {

      requestDto.getMultiMatchParams().forEach((key, value) -> {
        List<String> searchParamsCombinedList = new ArrayList<>();
        searchParamsCombinedList.add(key);

        for (String val : value) {
          searchParamsCombinedList.add(getSearchPath(val));
        }

        if (isSearchNestedField(value.get(0))) {
          query.should(getNestedShouldMultiMatchQuery(searchParamsCombinedList, getSearchNestedPath(value.get(0))));
        } else {
          query.should(QueryBuilders.multiMatchQuery(searchParamsCombinedList));
        }
      });

    }
  }

  private void buildExistsQuery(BoolQueryBuilder query, MerchantSearchRequestDto requestDto) {

    if (requestDto.getOperator() == SearchOperator.AND) {

      for (String param : requestDto.getExistsParams()) {
        if (isSearchNestedField(param)) {
          query.filter(getNestedFilterExistsQuery(param));
        } else {
          query.filter(QueryBuilders.existsQuery(getSearchPath(param)));
        }
      }

    } else {

      for (String param : requestDto.getExistsParams()) {
        if (isSearchNestedField(param)) {
          query.should(getNestedShouldExistsQuery(param));
        } else {
          query.should(QueryBuilders.existsQuery(getSearchPath(param)));
        }
      }

    }
  }

  private void buildRangeQuery(BoolQueryBuilder query, MerchantSearchRequestDto requestDto) {

    if (requestDto.getOperator() == SearchOperator.AND) {

      requestDto.getRangeParams().forEach((key, value) -> {
        if (isRangeNestedField(key)) {
          query.filter(getNestedFilterRangeQuery(key, value));
        } else {
          query.filter(QueryBuilders.rangeQuery(getRangeSearchPath(key)).gte(value.getGte()).lte(value.getLte()));
        }
      });


    } else {

      requestDto.getRangeParams().forEach((key, value) -> {
        if (isRangeNestedField(key)) {
          query.should(getNestedShouldRangeQuery(key, value));
        } else {
          query.should(QueryBuilders.rangeQuery(getRangeSearchPath(key)).gte(value.getGte()).lte(value.getLte()));
        }
      });

    }
  }


  private NestedQueryBuilder getNestedFilterTermsQuery(String key, List<String> value) {
    return QueryBuilders.nestedQuery(getSearchNestedPath(key),
        QueryBuilders.boolQuery().filter(QueryBuilders.termsQuery(getSearchPath(key), value)), ScoreMode.None);
  }

  private NestedQueryBuilder getNestedFilterWildCardQuery(String key, String value) {
    return QueryBuilders.nestedQuery(getSearchNestedPath(key),
        QueryBuilders.boolQuery().filter(QueryBuilders.wildcardQuery(getSearchPath(key), value)), ScoreMode.None);
  }

  private QueryBuilder getNestedFilterMultiMatchQuery(String nestedPath, String value,
      List<String> searchFieldsWithSearchPath) {
    return QueryBuilders.nestedQuery(nestedPath, QueryBuilders.boolQuery().filter(QueryBuilders.multiMatchQuery(value,
            searchFieldsWithSearchPath.toArray(new String[searchFieldsWithSearchPath.size()]))), ScoreMode.None);
  }

  private QueryBuilder getNestedFilterExistsQuery(String param) {
    return QueryBuilders.nestedQuery(getSearchNestedPath(param),
        QueryBuilders.boolQuery().filter(QueryBuilders.existsQuery(getSearchPath(param))), ScoreMode.None);
  }

  private QueryBuilder getNestedFilterRangeQuery(String key, RangeParamMinMax value) {
    return QueryBuilders.nestedQuery(getRangeNestedPath(key), QueryBuilders.boolQuery()
            .filter(QueryBuilders.rangeQuery(getRangeSearchPath(key)).gte(value.getGte()).lte(value.getLte())),
        ScoreMode.None);
  }


  private NestedQueryBuilder getNestedShouldTermsQuery(String key, List<String> value) {
    return QueryBuilders.nestedQuery(getSearchNestedPath(key),
        QueryBuilders.boolQuery().should(QueryBuilders.termsQuery(getSearchPath(key), value)), ScoreMode.None);
  }

  private NestedQueryBuilder getNestedShouldWildCardQuery(String key, String value) {
    return QueryBuilders.nestedQuery(getSearchNestedPath(key),
        QueryBuilders.boolQuery().should(QueryBuilders.wildcardQuery(getSearchPath(key), value)), ScoreMode.None);
  }

  private QueryBuilder getNestedShouldMultiMatchQuery(List<String> value, String nestedPath) {
    return QueryBuilders.nestedQuery(nestedPath, QueryBuilders.boolQuery().should(QueryBuilders.multiMatchQuery(value)),
        ScoreMode.None);
  }

  private QueryBuilder getNestedShouldExistsQuery(String param) {
    return QueryBuilders.nestedQuery(getSearchNestedPath(param),
        QueryBuilders.boolQuery().should(QueryBuilders.existsQuery(getSearchPath(param))), ScoreMode.None);
  }

  private QueryBuilder getNestedShouldRangeQuery(String key, RangeParamMinMax value) {
    return QueryBuilders.nestedQuery(getSearchNestedPath(key),
        QueryBuilders.boolQuery()
            .should(QueryBuilders.rangeQuery(getRangeSearchPath(key)).gte(value.getGte()).lte(value.getLte())),
        ScoreMode.None);
  }
}
