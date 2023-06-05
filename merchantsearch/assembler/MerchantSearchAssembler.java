// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.merchantsearch.assembler;

import com.paysafe.mastermerchant.config.FeatureProperties;
import com.paysafe.mastermerchant.merchantsearch.config.ElasticSearchMetaDataConfig;
import com.paysafe.mastermerchant.merchantsearch.dto.MerchantSearchDownloadRequestDto;
import com.paysafe.mastermerchant.merchantsearch.dto.MerchantSearchRequestDto;
import com.paysafe.mastermerchant.merchantsearch.dto.MerchantSearchResponseDto;
import com.paysafe.mastermerchant.merchantsearch.enums.DownloadDateFormat;
import com.paysafe.mastermerchant.merchantsearch.enums.DownloadTimeFormat;
import com.paysafe.mastermerchant.merchantsearch.enums.SearchOperator;
import com.paysafe.mastermerchant.merchantsearch.web.rest.resource.MerchantSearchDownloadRequestResource;
import com.paysafe.mastermerchant.merchantsearch.web.rest.resource.MerchantSearchRequestResource;
import com.paysafe.mastermerchant.merchantsearch.web.rest.resource.MerchantSearchResponseResource;
import com.paysafe.mastermerchant.util.DataConstants;
import com.paysafe.mpp.commons.dto.MasterMerchantDto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ReadContext;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Service
public class MerchantSearchAssembler {

  @Autowired
  private ElasticSearchMetaDataConfig elasticSearchMetaDataConfig;

  @Autowired
  private FeatureProperties featureProperties;

  private final Configuration jsonPathConfig = Configuration.builder().options(Option.SUPPRESS_EXCEPTIONS).build();

  private static final ObjectMapper objectMapper = new ObjectMapper();

  /**
   *
   * @param requestResource - request.
   * @return requestDto for search.
   */
  public MerchantSearchRequestDto getMerchantSearchRequestDto(MerchantSearchRequestResource requestResource) {

    MerchantSearchRequestDto requestDto = new MerchantSearchRequestDto();
    BeanUtils.copyProperties(requestResource, requestDto);

    setDefaultValuesForOffsetAndSort(requestDto);
    setDefaultValuesForSearchFields(requestDto);
    appendWilcardSymbolToSearchParams(requestDto);

    return requestDto;
  }

  private void setDefaultValuesForDownloadFields(MerchantSearchDownloadRequestDto requestDto) {

    if (requestDto.getDownloadFields() == null) {
      requestDto.setDownloadFields(Collections.emptyList());
    }

    if (StringUtils.isBlank(requestDto.getPreferredTimeZone())) {
      requestDto.setPreferredTimeZone("UTC");
    }

    if (requestDto.getResponseDateFormat() == null) {
      requestDto.setResponseDateFormat(DownloadDateFormat.MM_DD_YYYY);
    }

    if (requestDto.getResponseTimeFormat() == null) {
      requestDto.setResponseTimeFormat(DownloadTimeFormat.HR_24);
    }
  }

  /**
   *
   * returns merchantSearchResponseDto for merchant download api.
   */
  public MerchantSearchDownloadRequestDto getMerchantSearchDownloadRequestDto(
      MerchantSearchDownloadRequestResource requestResource) {

    // remove this after csc went to prod, backward compatability change
    if (requestResource.getDownloadFields() == null || requestResource.getDownloadFields().isEmpty()) {
      requestResource.setDownloadFields(requestResource.getResponseFields());
      requestResource.setResponseFields(null);
    }

    MerchantSearchDownloadRequestDto requestDto = new MerchantSearchDownloadRequestDto();
    BeanUtils.copyProperties(requestResource, requestDto);

    setDefaultValuesForOffsetAndSort(requestDto);
    setDefaultValuesForSearchFields(requestDto);
    setDefaultValuesForDownloadFields(requestDto);
    appendWilcardSymbolToSearchParams(requestDto);

    updateResponseFieldsToMatchElasticFields(requestDto);

    return requestDto;
  }

  private void setDefaultValuesForOffsetAndSort(MerchantSearchRequestDto requestDto) {
    if (requestDto.getOffset() == null) {
      requestDto.setOffset(0);
    }

    if (requestDto.getLimit() == null) {
      requestDto.setLimit(10);
    }

    if (requestDto.getSortOrder() == null) {
      requestDto.setSortOrder(SortOrder.ASC);
    }

    if (requestDto.getSortField() == null || requestDto.getSortField().isEmpty()) {
      requestDto.setSortField(DataConstants.MERCHANT_SEARCH_DEFAULT_SORT_FIELD);
    }

    if (requestDto.getOperator() == null) {
      requestDto.setOperator(SearchOperator.AND);
    }
  }

  private void setDefaultValuesForSearchFields(MerchantSearchRequestDto requestDto) {
    if (requestDto.getFilterParams() == null) {
      requestDto.setFilterParams(Collections.emptyMap());
    }

    if (requestDto.getSearchParams() == null) {
      requestDto.setSearchParams(Collections.emptyMap());
    }

    if (requestDto.getMultiMatchParams() == null) {
      requestDto.setMultiMatchParams(Collections.emptyMap());
    }

    if (requestDto.getExistsParams() == null) {
      requestDto.setExistsParams(Collections.emptyList());
    }

    if (requestDto.getRangeParams() == null) {
      requestDto.setRangeParams(Collections.emptyMap());
    }

    if (requestDto.getResponseFields() == null) {
      requestDto.setResponseFields(Collections.emptyList());
    }
  }

  private void appendWilcardSymbolToSearchParams(MerchantSearchRequestDto requestDto) {
    requestDto.getSearchParams()
        .forEach((key, value) -> requestDto.getSearchParams().put(key, getWildCardFieldValue(value)));
  }

  /**
   *
   * appends * for wild card search.
   */
  public String getWildCardFieldValue(String value) {
    if (value == null) {
      return null;
    } else {
      return new StringBuilder().append(DataConstants.WILDCARD_SYMBOL).append(value)
          .append(DataConstants.WILDCARD_SYMBOL).toString();
    }
  }

  /**
   *
   * returns merchantSearchResponseDto.
   */
  public MerchantSearchResponseResource getMerchantSearchResponseResource(MerchantSearchResponseDto responseDto) {
    MerchantSearchResponseResource responseResource = new MerchantSearchResponseResource();
    BeanUtils.copyProperties(responseDto, responseResource);

    return responseResource;
  }

  /**
   *
   * we get download field as ["merchantId"]. if we get downloadFields as empty we add all download fields to it. this
   * method also removes any duplicate values. this method updates response fields from "mechantId" to
   * "paymentAccounts.processingAccounts.sourceAuthority.referenceId"
   */
  public void updateResponseFieldsToMatchElasticFields(MerchantSearchDownloadRequestDto searchDto) {

    // if DownloadFields is empty we are bringing all downloadable fields
    if (searchDto.getDownloadFields().isEmpty()) {
      searchDto.setDownloadFields(new ArrayList<>(elasticSearchMetaDataConfig.getDownloadMapping().keySet()));
    }

    List<String> uniqueDownloadFields = searchDto.getDownloadFields().stream().distinct().collect(Collectors.toList());
    searchDto.setDownloadFields(uniqueDownloadFields);

    // setting the response fields for optimization
    List<String> responseFieldsInElasticFormat = new ArrayList<>();
    searchDto.getDownloadFields().forEach(downloadField -> responseFieldsInElasticFormat
        .add(elasticSearchMetaDataConfig.getDownloadMapping().get(downloadField).getSourcePath()));

    searchDto.setResponseFields(responseFieldsInElasticFormat);
  }

  /**
   *
   * this method returns the csv data which is in denormalised form.
   * 
   * @throws Exception - parsing exception
   */
  public List<String[]> getMerchantDataInDownloadableFormat(MerchantSearchDownloadRequestDto requestDto,
      MerchantSearchResponseDto responseDto) throws Exception {
    List<String[]> csvData = new ArrayList<>();

    csvData.add(getFileHeaders(requestDto));

    for (MasterMerchantDto merchant : responseDto.getMerchants()) {
      csvData.add(getDownloadRowData(merchant, requestDto));
    }
    return csvData;
  }

  /**
   *
   * this method returns Headers of download file.
   *
   */
  public String[] getFileHeaders(MerchantSearchDownloadRequestDto requestDto) {
    return requestDto.getDownloadFields().stream()
        .map(downloadField -> elasticSearchMetaDataConfig.getDownloadMapping().get(downloadField).getHeader())
        .collect(Collectors.toList()).toArray(new String[requestDto.getDownloadFields().size()]);
  }

  /**
   *
   * this method takes a single merchant and returns his data in denormalised form.
   *
   * @throws JsonProcessingException while getting data at a given path
   */
  public String[] getDownloadRowData(MasterMerchantDto merchant, MerchantSearchDownloadRequestDto requestDto)
      throws Exception {
    String[] rowData = new String[requestDto.getDownloadFields().size()];
    ReadContext ctx = JsonPath.using(jsonPathConfig).parse(objectMapper.writeValueAsString(merchant));
    String[] downloadDateFields = {DataConstants.ENABLED_DATE, DataConstants.CREATION_DATE};

    int arrayIndex = 0;
    for (String downloadField : requestDto.getDownloadFields()) {
      String jsonPath = elasticSearchMetaDataConfig.getDownloadMapping().get(downloadField).getJsonPath();
      String fieldValue = ctx.read(jsonPath, String.class);
      if (featureProperties.isTimezoneDownloadEnabled() && StringUtils.endsWithAny(downloadField, downloadDateFields)
          && StringUtils.isNotBlank(fieldValue)) {
        rowData[arrayIndex] = getFormattedDateValue(fieldValue, requestDto.getPreferredTimeZone(),
            requestDto.getResponseDateFormat(), requestDto.getResponseTimeFormat());
      } else {
        rowData[arrayIndex] = fieldValue;
      }
      arrayIndex++;
    }
    return rowData;
  }

  private String getFormattedDateValue(String fieldValue, String preferredTimeZone,
      DownloadDateFormat responseDateFormat, DownloadTimeFormat responseTimeFormat) throws Exception {
    DateFormat inputFormatter = new SimpleDateFormat(DataConstants.DATE_FIELD_ELASTIC_RESPONSE_FORMAT, Locale.US);
    Date fromDate = inputFormatter.parse(fieldValue);
    String outputDateTimeFormat =
        responseDateFormat.getDateFormat() + DataConstants.DATE_TIME_SEPARATOR + responseTimeFormat.getTimeFormat();
    DateFormat outputFormatter = new SimpleDateFormat(outputDateTimeFormat, Locale.US);
    TimeZone timeZone = TimeZone.getTimeZone(preferredTimeZone);
    outputFormatter.setTimeZone(timeZone);
    String formattedOutputDate = outputFormatter.format(fromDate);
    return formattedOutputDate + DataConstants.SPACE + preferredTimeZone;
  }

}
