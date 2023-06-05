// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.service.impl;

import com.paysafe.mastermerchant.config.Mapping;
import com.paysafe.mastermerchant.dataprocessing.repository.TemplateMetadataJpaRepository;
import com.paysafe.mastermerchant.dataprocessing.repository.model.TemplateMetadataEntity;
import com.paysafe.mastermerchant.repository.GroupSearchIndexRepository;
import com.paysafe.mastermerchant.service.SearchService;
import com.paysafe.mastermerchant.service.dto.SearchRequestDto;
import com.paysafe.mastermerchant.service.dto.SearchResponseDto;
import com.paysafe.mastermerchant.service.dto.TemplateMetadataDto;

import com.jayway.jsonpath.JsonPath;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation.Bucket;
import org.elasticsearch.search.aggregations.bucket.SingleBucketAggregation;
import org.elasticsearch.search.aggregations.metrics.NumericMetricsAggregation.SingleValue;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * SearchServiceImpl.java.
 * 
 * @author abhineetagarwal
 *
 * 
 */
@Service
public class SearchServiceImpl implements SearchService {

  private static final String FIELD_TYPE_LIST = "List";

  private final GroupSearchIndexRepository groupSearchIndexRepository;

  private final TemplateMetadataJpaRepository templateMetadataJpaRepository;

  private static final Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);

  /**
   * Constructor.
   * 
   * @param groupSearchIndexRepository elasticsearch repo.
   */
  @Autowired
  public SearchServiceImpl(GroupSearchIndexRepository groupSearchIndexRepository,
      TemplateMetadataJpaRepository templateMetadataJpaRepository) {
    this.groupSearchIndexRepository = groupSearchIndexRepository;
    this.templateMetadataJpaRepository = templateMetadataJpaRepository;
  }

  @Override
  public SearchResponseDto getRecords(SearchRequestDto searchRequest) {
    SearchResponseDto searchResponse = new SearchResponseDto();
    searchResponse.setRecords(new ArrayList<>());
    String templateName = getTemplateName(searchRequest.getTenant(), searchRequest.getTemplateName());
    SearchResponse response = groupSearchIndexRepository.searchScript(searchRequest.getTerms(), templateName);
    boolean isAggregation = !(response.getAggregations() == null || response.getAggregations().asMap().isEmpty());
    if (isAggregation) {
      computeAggregations(response.getAggregations(), new HashMap<>(), searchResponse);
    } else if (response.getHits() != null && response.getHits().getHits().length > 0) {
      TemplateMetadataEntity entity =
          templateMetadataJpaRepository.findByTenantAndName(searchRequest.getTenant(), searchRequest.getTemplateName());

      logger.info("logging the templateMetaData {}", entity);

      if (entity != null) {
        Map<String, Mapping> mappings = entity.getResponseMappings();
        response.getHits().forEach(hit -> {
          Map<String, Object> sourceFields = new HashMap<>();
          JSONObject jsonObject = new JSONObject(hit.getSourceAsMap());
          for (Entry<String, Mapping> mappingEntry : mappings.entrySet()) {
            Mapping currentMapping = mappingEntry.getValue();
            Object value = JsonPath.read(jsonObject, currentMapping.getPath());
            sourceFields.put(mappingEntry.getKey(), getSanitizedValue(value, currentMapping.getType()));
          }
          searchResponse.getRecords().add(sourceFields);
        });
      }

    }
    return searchResponse;
  }

  /**
   * Get template name.
   *
   * @param tenant tenant
   * @param name templateName
   * @return unique combination of tenant and name
   */
  public String getTemplateName(String tenant, String name) {
    return StringUtils.joinWith("_", tenant, name);
  }

  private Object getSanitizedValue(Object value, String type) {
    Set<String> valueSet = new HashSet<>();
    if (value instanceof JSONArray) {
      JSONArray jsonArray = (JSONArray) value;
      for (int i = 0; i < jsonArray.length(); i++) {
        if (!StringUtils.isBlank(jsonArray.optString(i))) {
          valueSet.add(jsonArray.optString(i));
        }
      }
    } else if (value instanceof String) {
      valueSet.add((String) value);
    } else {
      logger.error("Found unknown dataType while sanitizing {} - to type {} , dataType {}", value, type,
          value.getClass());
    }
    List<String> values = new ArrayList<>(valueSet);

    if (FIELD_TYPE_LIST.equalsIgnoreCase(type)) {
      return values;
    }
    if (valueSet.size() > 1) {
      logger.error("Found more than one value for type {} , value {} ", type, value);
    }
    return valueSet.isEmpty() ? null : values.get(0);
  }

  /**
   * This methods computes aggregations and stores each as a record in search
   * response.
   * 
   * @param aggregations aggregations
   * @param result aggregation result to be stored and passed for each nested
   *          call
   * @param searchResponse response to be returned
   */
  private void computeAggregations(Aggregations aggregations, Map<String, Object> result,
      SearchResponseDto searchResponse) {
    if (aggregations == null || aggregations.asMap().isEmpty()) {
      searchResponse.getRecords().add(new HashMap<>(result));
      return;
    }
    aggregations.asMap().keySet().forEach(aggregationName -> {
      Aggregation aggregationParentClass = aggregations.get(aggregationName);
      if (aggregationParentClass instanceof MultiBucketsAggregation) {
        computeMultiBucketAggregate(result, searchResponse, aggregationName, aggregationParentClass);
      } else if (aggregationParentClass instanceof SingleBucketAggregation) {
        SingleBucketAggregation parentAggregation = (SingleBucketAggregation) aggregationParentClass;
        computeAggregations(parentAggregation.getAggregations(), result, searchResponse);
      } else if (aggregationParentClass instanceof SingleValue) {
        SingleValue parentAggregation = (SingleValue) aggregationParentClass;
        result.put(aggregationName, parentAggregation.getValueAsString());
        computeAggregations(null, result, searchResponse);
      }
    });
  }

  private void computeMultiBucketAggregate(Map<String, Object> result, SearchResponseDto searchResponse,
      String aggregationName, Aggregation aggregationParentClass) {
    MultiBucketsAggregation parentAggregation = (MultiBucketsAggregation) aggregationParentClass;
    List<? extends Bucket> buckets = parentAggregation.getBuckets();
    if (buckets.isEmpty()) {
      return;
    }
    buckets.forEach(bucket -> {
      result.put(aggregationName, bucket.getKey());
      computeAggregations(bucket.getAggregations(), result, searchResponse);
    });
  }

  @Override
  public void saveTemplate(TemplateMetadataDto templateMetadata) {
    templateMetadataJpaRepository.save(dtoToEntity(templateMetadata));
  }

  private TemplateMetadataEntity dtoToEntity(TemplateMetadataDto dto) {
    TemplateMetadataEntity entity = new TemplateMetadataEntity();
    BeanUtils.copyProperties(dto, entity);
    entity.setId(entity.getTenant() + "_" + entity.getName());
    return entity;
  }

}