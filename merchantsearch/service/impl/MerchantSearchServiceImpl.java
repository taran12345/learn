// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.merchantsearch.service.impl;

import com.paysafe.mastermerchant.merchantsearch.assembler.MerchantSearchAssembler;
import com.paysafe.mastermerchant.merchantsearch.dto.MerchantSearchRequestDto;
import com.paysafe.mastermerchant.merchantsearch.dto.MerchantSearchResponseDto;
import com.paysafe.mastermerchant.merchantsearch.service.MerchantSearchQueryBuilderService;
import com.paysafe.mastermerchant.merchantsearch.service.MerchantSearchService;
import com.paysafe.mastermerchant.merchantsearch.web.rest.resource.MerchantSearchRequestResource;
import com.paysafe.mastermerchant.repository.GroupSearchIndexRepository;
import com.paysafe.mastermerchant.util.DataConstants;
import com.paysafe.mastermerchant.util.ElasticsearchUtil;
import com.paysafe.mpp.commons.dto.CardConfiguration;
import com.paysafe.mpp.commons.dto.MasterMerchantDto;
import com.paysafe.mpp.commons.dto.ProcessingAccountDto.CardType;
import com.paysafe.mpp.commons.service.CryptoService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.opencsv.CSVWriter;

import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

@Service
public class MerchantSearchServiceImpl implements MerchantSearchService {

  @Autowired
  private MerchantSearchQueryBuilderService merchantSearchQueryBuilderService;

  @Autowired
  private GroupSearchIndexRepository groupSearchIndexRepository;

  @Autowired
  private CryptoService cryptoService;

  @Autowired
  private MerchantSearchAssembler merchantSearchAssembler;

  @Autowired
  private ElasticsearchUtil elasticsearchUtil;

  private static final ObjectMapper objectMapper = new ObjectMapper();

  private static final Gson gson = new Gson();

  private static final Logger logger = LoggerFactory.getLogger(MerchantSearchServiceImpl.class);

  /**
   * builds sort query.
   * 
   * @throws Exception
   * 
   */
  public MerchantSearchResponseDto processMerchantSearchRequest(MerchantSearchRequestDto requestDto) throws Exception {
    BoolQueryBuilder searchQueryBuilder = merchantSearchQueryBuilderService.getMerchantSearchRequestBuilder(requestDto);

    SortBuilder sortQueryBuilder = merchantSearchQueryBuilderService.buildSortQuery(requestDto);

    SearchResponse searchResults = groupSearchIndexRepository.search(requestDto, searchQueryBuilder, sortQueryBuilder,
        merchantSearchQueryBuilderService.prepareAggregations(requestDto.getDistinctFields()));

    return getMerchantSearchResponseDto(searchResults, requestDto.getOffset(), requestDto.getLimit());
  }

  /**
   * builds sort query.
   * 
   * @throws Exception
   * 
   */
  public MerchantSearchResponseDto getMerchantSearchResponseDto(SearchResponse searchResults, Integer offset,
      Integer limit) throws Exception {

    MerchantSearchResponseDto responseDto = new MerchantSearchResponseDto();
    responseDto.setOffset(offset);
    responseDto.setLimit(limit);

    Map<String, List<String>> responseMap = new HashMap<>();
    try {
      elasticsearchUtil.putResultsToMap(responseMap, searchResults.getAggregations());
    } catch (Exception ex) {
      logger.error("Failed to populate aggregations..{}", ex.getMessage(), ex);
    }
    responseDto.setDistinct(responseMap);

    if (searchResults.getHits() == null) {
      responseDto.setTotalCount(DataConstants.ZERO);
      return responseDto;
    }

    responseDto.setTotalCount(searchResults.getHits().getTotalHits());

    List<MasterMerchantDto> merchants = new ArrayList<>();
    for (SearchHit searchResult : searchResults.getHits()) {
      Map<String, Object> resultMap = searchResult.getSourceAsMap();

      String searchResultJson = gson.toJson(resultMap);
      try {
        MasterMerchantDto masterMerchant = objectMapper.readValue(searchResultJson, MasterMerchantDto.class);
        masterMerchant = (MasterMerchantDto) cryptoService.decrypt(masterMerchant);
        merchants.add(masterMerchant);
      } catch (Exception ex) {
        logger.error("Conversion from json to masterMerchant object failed with exception {}", ex.getMessage(), ex);
        throw ex;
      }
    }

    responseDto.setMerchants(merchants);
    return responseDto;
  }

  /**
   * writes csvdata to servlet response.
   * 
   * @throws IOException
   * 
   */
  public void writeSearchResponseToOutputStream(List<String[]> csvData, HttpServletResponse servletResponse)
      throws IOException {
    servletResponse.setContentType(DataConstants.OCTET_STREAM_MEDIA_TYPE);
    servletResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION, DataConstants.ATTACHMENT_FILENAME);

    try (CSVWriter writer =
        new CSVWriter(new OutputStreamWriter(servletResponse.getOutputStream(), StandardCharsets.UTF_8))) {
      writer.writeAll(csvData);
    }

  }

  /**
   * Get referenceids for a given MID.
   * 
   */
  public List<String> getReferenceIdsForEnabledMid(String mid) throws Exception {
    Map<String, List<String>> filterParams = new HashMap<>();
    filterParams.put(DataConstants.MID, Collections.singletonList(mid));
    filterParams.put(DataConstants.MID_ENABLED, Collections.singletonList(DataConstants.TRUE));

    MerchantSearchRequestDto searchDto =
        merchantSearchAssembler.getMerchantSearchRequestDto(new MerchantSearchRequestResource());
    searchDto.setFilterParams(filterParams);
    MerchantSearchResponseDto responseDto = processMerchantSearchRequest(searchDto);
    return responseDto.getMerchants().stream().filter(a -> hasMidAsEnabled(a, mid))
        .map(a -> a.getSourceAuthority().getReferenceId()).collect(Collectors.toList());
  }

  private boolean hasMidAsEnabled(MasterMerchantDto merchantDetails, String mid) {

    CardConfiguration cardConfiguration =
        merchantDetails.getPaymentAccounts().get(0).getProcessingAccounts().get(0).getCardConfiguration();
    if (cardConfiguration == null) {
      return false;
    }
    List<CardType> cardTypes = cardConfiguration.getCardTypes();
    if (CollectionUtils.isEmpty(cardTypes)) {
      return false;
    }
    return cardTypes.stream()
        .anyMatch(cardType -> mid.equals(cardType.getAcquirer().getMid()) && cardType.getEnabled());
  }
}
