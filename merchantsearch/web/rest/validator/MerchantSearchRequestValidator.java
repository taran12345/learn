// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.merchantsearch.web.rest.validator;

import com.paysafe.mastermerchant.merchantsearch.config.ElasticSearchMetaDataConfig;
import com.paysafe.mastermerchant.merchantsearch.dto.MerchantSearchDownloadRequestDto;
import com.paysafe.mastermerchant.merchantsearch.dto.MerchantSearchRequestDto;
import com.paysafe.mastermerchant.merchantsearch.web.rest.resource.MerchantSearchRequestResource;
import com.paysafe.op.errorhandling.exceptions.BadRequestException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MerchantSearchRequestValidator {

  @Autowired
  private ElasticSearchMetaDataConfig elasticSearchMetaDataConfig;

  /**
   * Validates master merchant search request resource.
   * 
   */
  public void validateMerchantSearchRequestDto(MerchantSearchRequestDto requestDto) {
    validateFilterParams(requestDto);
    validateSearchParams(requestDto);
    validateDistinctFields(requestDto.getDistinctFields());
    validateMultiMatchParams(requestDto);
    validateExistsParams(requestDto);
    validateRangeParams(requestDto);

    validateSortField(requestDto);
    validateSortFieldForSearchAfter(requestDto);
    validateSearchAfterParam(requestDto);

    validateOffsetAndLimit(requestDto);
  }

  /**
   * Validates master merchant search request resource.
   * 
   */
  public void validateMerchantSearchDownloadRequestDto(MerchantSearchDownloadRequestDto requestDto) {
    validateFilterParams(requestDto);
    validateSearchParams(requestDto);
    validateMultiMatchParams(requestDto);
    validateExistsParams(requestDto);
    validateRangeParams(requestDto);

    validateDownloadFields(requestDto);

    validateSortField(requestDto);
    validateSortFieldForSearchAfter(requestDto);
    validateSearchAfterParam(requestDto);

    validateOffsetAndLimit(requestDto);
  }

  /**
   *
   * validates filter params.
   */
  public void validateFilterParams(MerchantSearchRequestDto requestDto) {
    for (Map.Entry<String, List<String>> filterParam : requestDto.getFilterParams().entrySet()) {
      if (!elasticSearchMetaDataConfig.getSearchMapping().containsKey(filterParam.getKey())) {
        throw BadRequestException.builder().unsupportedOperation()
            .details("Given filter Params contains unsupported keys").build();
      }

      if (filterParam.getValue() == null || filterParam.getValue().isEmpty() || filterParam.getValue().contains("")
          || filterParam.getValue().contains(null)) {
        throw BadRequestException.builder().unsupportedOperation()
            .details("Given filter Params contains unsupported values").build();
      }
    }
  }

  /**
   *
   * validates search params.
   */
  public void validateSearchParams(MerchantSearchRequestDto requestDto) {
    for (Map.Entry<String, String> searchParam : requestDto.getSearchParams().entrySet()) {
      if (!elasticSearchMetaDataConfig.getSearchMapping().containsKey(searchParam.getKey())) {
        throw BadRequestException.builder().unsupportedOperation()
            .details("Given search Params contains unsupported keys").build();
      }

      if (StringUtils.isBlank(searchParam.getValue())) {
        throw BadRequestException.builder().unsupportedOperation()
            .details("Given search Params contains unsupported values").build();
      }
    }
  }

  /**
   * Validates provided fields.
   */
  public void validateDistinctFields(List<String> distinctFields) {
    if (CollectionUtils.isEmpty(distinctFields)) {
      return;
    }
    String invalidFields = distinctFields.parallelStream()
        .filter(field -> !elasticSearchMetaDataConfig.getSearchMapping().containsKey(field))
        .collect(Collectors.joining(", "));
    if (StringUtils.isNotBlank(invalidFields)) {
      throw BadRequestException.builder().unsupportedOperation()
          .details(String.format("Given unsupported field(s) %s", invalidFields)).build();
    }
  }

  /**
   *
   * validates multi match params.
   */
  public void validateMultiMatchParams(MerchantSearchRequestDto requestDto) {
    for (Map.Entry<String, List<String>> multiMatchParam : requestDto.getMultiMatchParams().entrySet()) {
      if (!elasticSearchMetaDataConfig.getSearchMapping().keySet().containsAll(multiMatchParam.getValue())) {
        throw BadRequestException.builder().unsupportedOperation()
            .details("Given multiMatch Params contains unsupported values").build();
      }

      if (StringUtils.isBlank(multiMatchParam.getKey())) {
        throw BadRequestException.builder().unsupportedOperation()
            .details("Given multiMatch Params contains unsupported keys").build();
      }
    }
  }

  /**
   *
   * validates exists params.
   */
  public void validateExistsParams(MerchantSearchRequestDto requestDto) {
    if (!requestDto.getExistsParams().isEmpty()
        && !elasticSearchMetaDataConfig.getSearchMapping().keySet().containsAll(requestDto.getExistsParams())) {
      throw BadRequestException.builder().unsupportedOperation()
          .details("Given exists Params contains unsupported values").build();
    }
  }

  /**
   *
   * validates range params.
   */
  public void validateRangeParams(MerchantSearchRequestDto requestDto) {
    for (Map.Entry<String, MerchantSearchRequestDto.RangeParamMinMax> rangeParam : requestDto.getRangeParams()
        .entrySet()) {
      if (!elasticSearchMetaDataConfig.getRangeMapping().containsKey(rangeParam.getKey())) {
        throw BadRequestException.builder().unsupportedOperation()
            .details("Given range Params contains unsupported keys").build();
      }

      if (rangeParam.getValue() == null || StringUtils.isBlank(rangeParam.getValue().getGte())
          || StringUtils.isBlank(rangeParam.getValue().getLte())) {
        throw BadRequestException.builder().unsupportedOperation()
            .details("Given range Params contains unsupported values").build();
      }
    }
  }

  /**
   *
   * validates download params.
   */
  public void validateDownloadFields(MerchantSearchDownloadRequestDto requestDto) {
    if (!elasticSearchMetaDataConfig.getDownloadMapping().keySet().containsAll(requestDto.getDownloadFields())) {
      throw BadRequestException.builder().unsupportedOperation()
          .details("Given download params contains unsupported value").build();
    }
  }

  /**
   *
   * validates sort params.
   */
  public void validateSortField(MerchantSearchRequestDto requestDto) {
    if (!elasticSearchMetaDataConfig.getSortMapping().containsKey(requestDto.getSortField())) {
      throw BadRequestException.builder().unsupportedOperation()
          .details("Given sort params contains unsupported values")
          .build();
    }
  }

  /**
   *
   * validates if given sort param is accepted for search after.
   */
  public void validateSortFieldForSearchAfter(MerchantSearchRequestDto requestDto) {

    if (requestDto.getSearchAfter() != null) {
      List<String> searchAfterSupportedSortkeys = elasticSearchMetaDataConfig.getSortMapping().entrySet().stream()
          .filter(map -> map.getValue().getIsSearchAfterSupported()).map(map -> map.getKey())
          .collect(Collectors.toList());

      if (!searchAfterSupportedSortkeys.contains(requestDto.getSortField())) {
        throw BadRequestException.builder().unsupportedOperation()
            .details("Invalid sort key for the given searchAfter param").build();
      }
    }
  }

  /**
   *
   * validates the value of searchAfter to be non empty.
   */
  public void validateSearchAfterParam(MerchantSearchRequestDto requestDto) {
    if (requestDto.getSearchAfter() != null && requestDto.getSearchAfter().isEmpty()) {
      throw BadRequestException.builder().unsupportedOperation()
          .details("Search After should not be empty value").build();
    }
  }

  /**
   *
   * validates offset and limit.
   */
  public void validateOffsetAndLimit(MerchantSearchRequestDto searchDto) {
    if (searchDto.getOffset() + searchDto.getLimit() > 10000) {
      throw BadRequestException.builder().unsupportedOperation()
          .details("Cannot search in more than the permitted range of 10000 records. please refine your query").build();
    }
  }

  /**
   *
   * validates merchant search request resource.
   */
  public void validateMerchantSearchRequestResource(MerchantSearchRequestResource requestResource) {
    validateSortOrderAndSortFieldInResource(requestResource);
    validateOffsetAndSearchAfter(requestResource);
  }
    
  /**
   *
   * validates sortFields and sortOrder when searchAfter is given.
   */
  public void validateSortOrderAndSortFieldInResource(MerchantSearchRequestResource requestResource) {
    if (requestResource.getSearchAfter() != null
        && (requestResource.getSortField() == null || requestResource.getSortOrder() == null)) {
      throw BadRequestException.builder().unsupportedOperation()
          .details("When querying with search after please both sortField and sortOrder").build();
    }
  }

  /**
   *
   * validates offSet when searchAfter is given.
   */
  public void validateOffsetAndSearchAfter(MerchantSearchRequestResource requestResource) {
    if (requestResource.getSearchAfter() != null && requestResource.getOffset() != null) {
      throw BadRequestException.builder().unsupportedOperation()
          .details("When querying with Search after offset is not allowed").build();
    }
  }


}
