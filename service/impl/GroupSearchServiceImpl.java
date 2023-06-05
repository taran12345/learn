// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.service.impl;

import com.paysafe.mastermerchant.repository.GroupSearchIndexRepository;
import com.paysafe.mastermerchant.repository.MerchantAccountIndexRepository;
import com.paysafe.mastermerchant.service.GroupSearchService;
import com.paysafe.mastermerchant.service.dto.GroupSearchRequestDto;
import com.paysafe.mastermerchant.service.dto.MasterMerchantResponseDto;
import com.paysafe.mastermerchant.service.dto.MerchantCountsSummaryDto;
import com.paysafe.mastermerchant.util.DataConstants;
import com.paysafe.mastermerchant.util.GroupSearchUtil;
import com.paysafe.mastermerchant.util.LogUtil;
import com.paysafe.mastermerchant.validator.MasterMerchantValidator;
import com.paysafe.mastermerchant.web.rest.assembler.MasterMerchantAssembler;
import com.paysafe.mpp.commons.dto.MasterMerchantDto;
import com.paysafe.mpp.commons.dto.MasterMerchantDto.MasterMerchantDtoBuilder;
import com.paysafe.mpp.commons.dto.MasterPaymentAccountDto;
import com.paysafe.mpp.commons.dto.MasterPaymentAccountDto.MasterPaymentAccountDtoBuilder;
import com.paysafe.mpp.commons.dto.ProcessingAccountDto;
import com.paysafe.mpp.commons.service.CryptoService;
import com.paysafe.op.commons.indexdb.dto.BaseIndexDto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.bucket.nested.InternalNested;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * GroupSearchServiceImpl.java.
 *
 * @author kamarapuprabhath
 */

@Service
public class GroupSearchServiceImpl implements GroupSearchService {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  private static final Gson gson = new Gson();

  private static final Logger logger = LoggerFactory.getLogger(GroupSearchServiceImpl.class);

  private static final String TYPE = "searchData";

  private final GroupSearchIndexRepository groupSearchIndexRepository;

  private final MerchantAccountIndexRepository merchantAccountIndexRepository;

  private final DateFormatConverter dateFormatConverter;

  private final CryptoService cryptoService;


  /**
   * Constructor.
   *
   * @param groupSearchIndexRepository elasticsearch repo.
   * @param dateFormatConverter converts dateFormat to dates.
   */
  @Autowired
  public GroupSearchServiceImpl(GroupSearchIndexRepository groupSearchIndexRepository,
      DateFormatConverter dateFormatConverter, CryptoService cryptoService,
      MerchantAccountIndexRepository merchantAccountIndexRepository) {
    this.groupSearchIndexRepository = groupSearchIndexRepository;
    this.dateFormatConverter = dateFormatConverter;
    this.cryptoService = cryptoService;
    this.merchantAccountIndexRepository = merchantAccountIndexRepository;
  }

  @Override
  public void encryptAndSaveToElastic(MasterMerchantDto masterMerchantDto) {

    if (!MasterMerchantValidator.isValid(masterMerchantDto)) {
      String referenceIdSanitized = LogUtil.sanitizeInput(masterMerchantDto.getSourceAuthority().getReferenceId());
      logger.error("Elastic storage - invalid masterMerchant account {}", referenceIdSanitized);
      return;
    }

    String storageId = MasterMerchantAssembler.getElasticStorageId(masterMerchantDto);
    MasterMerchantDto encryptedMasterMerchantDto = (MasterMerchantDto) cryptoService.encrypt(masterMerchantDto);
    Map<String, Object> masterMerchantMap = objectMapper.convertValue(encryptedMasterMerchantDto, Map.class);
    masterMerchantMap.values().removeIf(Objects::isNull);

    BaseIndexDto indexDto = new BaseIndexDto();
    indexDto.setId(storageId);
    indexDto.setDocument(masterMerchantMap);
    indexDto.setType(TYPE);
    String id = merchantAccountIndexRepository.index(indexDto);
    logger.info("Elastic storage - Saved master merchant record to elastic - {}", id);
  }

  @Override
  public void saveGroup(MasterMerchantDto masterMerchant) {
    String sanitizedId = LogUtil.sanitizeInput(masterMerchant.getId());
    logger.info("Preparing masterMerchant data to store in elastic {}", sanitizedId);
    if (CollectionUtils.isEmpty(masterMerchant.getPaymentAccounts()) || !isPaymentAccountExists(masterMerchant)) {
      saveMasterMerchant(masterMerchant);
      return;
    }
    MasterMerchantDtoBuilder masterMerchantBuilder = masterMerchant.toBuilder();
    masterMerchantBuilder.paymentAccountIds(null).paymentAccounts(null);
    for (MasterPaymentAccountDto paymentAccount : masterMerchant.getPaymentAccounts()) {
      if (paymentAccount != null) {
        MasterPaymentAccountDtoBuilder paymentAccountBuilder = paymentAccount.toBuilder();
        List<ProcessingAccountDto> processingAccounts = paymentAccount.getProcessingAccounts();
        if (CollectionUtils.isEmpty(processingAccounts)) {
          masterMerchantBuilder.paymentAccounts(Arrays.asList(paymentAccountBuilder.build()));
          masterMerchantBuilder.paymentAccountIds(Arrays.asList(paymentAccount.getId()));
          saveMasterMerchant(masterMerchantBuilder.build());
          logger.info("Payment account data stored in elastic for id {}", sanitizedId);
          continue;
        }
        paymentAccountBuilder.processingAccountIds(null).processingAccounts(null);
        for (ProcessingAccountDto processingAccount : processingAccounts) {
          paymentAccountBuilder.processingAccounts(Arrays.asList(processingAccount));
          paymentAccountBuilder.processingAccountIds(Arrays.asList(processingAccount.getId()));
          masterMerchantBuilder.paymentAccounts(Arrays.asList(paymentAccountBuilder.build()));
          masterMerchantBuilder.paymentAccountIds(Arrays.asList(paymentAccount.getId()));
          saveMasterMerchant(masterMerchantBuilder.build());
          logger.info("Payment and processing account data stored in elastic for id {}", sanitizedId);
        }
      }
    }
  }

  private boolean isPaymentAccountExists(MasterMerchantDto masterMerchant) {
    for (MasterPaymentAccountDto paymentAccount : masterMerchant.getPaymentAccounts()) {
      if (paymentAccount != null) {
        return true;
      }
    }
    return false;
  }

  @Override
  public MasterMerchantResponseDto findSimilarGroupDto(GroupSearchRequestDto groupSearchDto) throws IOException {
    Map<String, Object> groupSearchMap = objectMapper.convertValue(groupSearchDto, Map.class);
    groupSearchMap.values().removeIf(Objects::isNull);
    SearchResponse response =
        groupSearchIndexRepository.searchScript(groupSearchMap, DataConstants.ADVANCED_SEARCH_TEMPLATE);

    return getMasterMerchantResponseDto(response, groupSearchDto.getOffset(), groupSearchDto.getLimit());
  }

  @Override
  public MasterMerchantResponseDto smartSearch(String searchQuery, Integer offset, Integer limit) throws IOException {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("offset", offset);
    parameters.put("limit", limit);
    parameters.put("smartsearchquerystring", searchQuery);
    SearchResponse response = groupSearchIndexRepository.searchScript(parameters, DataConstants.SMART_SEARCH_TEMPLATE);

    return getMasterMerchantResponseDto(response, offset, limit);
  }

  private void saveMasterMerchant(MasterMerchantDto masterMerchant) {
    if (masterMerchant.getId() == null) {
      logger.error("The given record does not have a group Id");
      return;
    }
    StringBuilder storageId = new StringBuilder();
    storageId.append(masterMerchant.getId());

    Map<String, Object> document = getDocument(storageId.toString());
    logger.info("Retrieved index document for id {}",masterMerchant.getId());

    if (GroupSearchUtil.paymentAccountExists(masterMerchant)) {
      groupDocumentExists(storageId, document);
      String paymentAccountId = GroupSearchUtil.getFirstPaymentAccountId(masterMerchant);
      storageId.append("-").append(paymentAccountId);

      Map<String, Object> documentWithPaymentAccounts = getDocument(storageId.toString());
      MasterPaymentAccountDto paymentAccountDto = GroupSearchUtil.getFirstPaymentAccount(masterMerchant);
      if (GroupSearchUtil.processingAccountExists(paymentAccountDto)) {
        paymentAccountDocumentExists(storageId, documentWithPaymentAccounts);
        String processingAccountId = GroupSearchUtil.getFirstProcessingAccountId(masterMerchant);
        storageId.append("-").append(processingAccountId);
      }
    }
    BaseIndexDto indexDto = new BaseIndexDto();
    indexDto.setId(storageId.toString());
    MasterMerchantDto encryptedMasterMerchantDto = (MasterMerchantDto) cryptoService.encrypt(masterMerchant);
    logger.info("Created encryptedMasterMerchantDto for id {}",masterMerchant.getId());
    Map<String, Object> masterMerchantMap = objectMapper.convertValue(encryptedMasterMerchantDto, Map.class);
    logger.info("Created masterMerchantMap for id {}",masterMerchant.getId());
    masterMerchantMap.values().removeIf(Objects::isNull);
    dateFormatConverter.convert(masterMerchantMap);
    indexDto.setDocument(masterMerchantMap);
    indexDto.setType(TYPE);
    String id = merchantAccountIndexRepository.index(indexDto);
    logger.info("Saved Group to elastic " + id);
  }

  private void paymentAccountDocumentExists(StringBuilder storageId, Map<String, Object> documentWithPaymentAccounts) {
    if (documentWithPaymentAccounts != null) {
      logger.info("Deleting records without ProcessingAccount.");
      deleteDocument(storageId.toString());
      logger.info("Deleted records without ProcessingAccount.");
    }
  }

  private void groupDocumentExists(StringBuilder storageId, Map<String, Object> document) {
    if (document != null) {
      logger.info("Deleting records without PaymentAccount.");
      deleteDocument(storageId.toString());
      logger.info("Deleted records without PaymentAccount.");
    }
  }

  private Map<String, Object> getDocument(String id) {
    BaseIndexDto baseIndexDto = new BaseIndexDto();
    baseIndexDto.setId(id);
    return groupSearchIndexRepository.getDocument(baseIndexDto);

  }

  private void deleteDocument(String id) {
    BaseIndexDto baseIndexDto = new BaseIndexDto();
    baseIndexDto.setId(id);
    baseIndexDto.setType(TYPE);
    groupSearchIndexRepository.deleteDocument(baseIndexDto);
  }

  private MasterMerchantResponseDto getMasterMerchantResponseDto(SearchResponse searchResults,
      Integer offsetValue, Integer limitValue) throws IOException {
    MasterMerchantResponseDto mmResponseDto = new MasterMerchantResponseDto();
    mmResponseDto.setOffset(offsetValue);
    mmResponseDto.setLimit(limitValue);

    if (searchResults.getHits() == null) {
      mmResponseDto.setTotalSearchMatches(DataConstants.ZERO);
      return mmResponseDto;
    }

    mmResponseDto.setTotalSearchMatches(searchResults.getHits().getTotalHits());

    List<MasterMerchantDto> merchants = new ArrayList<>();
    for (SearchHit searchResult : searchResults.getHits()) {
      Map<String, Object> result = searchResult.getSourceAsMap();
      result.put(DataConstants.ID, searchResult.getId());

      String searchResultJson = gson.toJson(result);
      try {
        MasterMerchantDto masterMerchant = objectMapper.readValue(searchResultJson, MasterMerchantDto.class);
        masterMerchant = (MasterMerchantDto) cryptoService.decrypt(masterMerchant);
        merchants.add(masterMerchant);
      } catch (Exception ex) {
        logger.error("Conversion from json to masterMerchant object failed with exception ", ex);
        throw ex;
      }
    }
    mmResponseDto.setMerchants(merchants);

    return mmResponseDto;
  }

  public List<MerchantCountsSummaryDto> getMerchantAggregationInfoBaseOnAccountStatus() {
    SearchResponse searchRespones = groupSearchIndexRepository.getMerchantAggregationInfoBaseOnAccountStatus();
    return getMerchantCountSummaryDto(searchRespones);
  }

  private List<MerchantCountsSummaryDto> getMerchantCountSummaryDto(SearchResponse response) {
    logger.info("Transforming Search Response to MerchantCountsSummaryDto");
    InternalNested paymentAccountsAggregation = response.getAggregations().get(DataConstants.PAYMENT_ACCOUNTS);
    InternalNested processingAccountsAggregation =
        paymentAccountsAggregation.getAggregations().get(DataConstants.PROCESSING_ACCOUNTS);
    Terms originLevelInfo = processingAccountsAggregation.getAggregations().get("originLevelInfo");
    List<MerchantCountsSummaryDto> merchantAggregationInfo = new ArrayList<>();
    for (Terms.Bucket originInfo : originLevelInfo.getBuckets()) {
      Terms accountStatusWiseCountsTermsInfo = originInfo.getAggregations().get("accountStatusWiseCounts");
      for (Terms.Bucket countBucket : accountStatusWiseCountsTermsInfo.getBuckets()) {
        MerchantCountsSummaryDto merchantCountsSummaryDto = new MerchantCountsSummaryDto();
        merchantCountsSummaryDto.setDocumentId(
            originInfo.getKeyAsString().toLowerCase(Locale.US) + "#" + DataConstants.MERCHANT_SUMMARY_COUNT_STATUS_TYPE
                + "#" + countBucket.getKeyAsString().toLowerCase(Locale.US));
        merchantCountsSummaryDto.setCounts(Long.valueOf(countBucket.getDocCount()));
        merchantCountsSummaryDto.setValue(countBucket.getKeyAsString());
        merchantCountsSummaryDto.setType(DataConstants.MERCHANT_SUMMARY_COUNT_STATUS_TYPE);
        merchantCountsSummaryDto.setOrigin(originInfo.getKeyAsString());
        merchantAggregationInfo.add(merchantCountsSummaryDto);
      }
      MerchantCountsSummaryDto merchantCountsSummaryDto = new MerchantCountsSummaryDto();
      merchantCountsSummaryDto.setDocumentId(originInfo.getKeyAsString().toLowerCase(Locale.US) + "#"
          + DataConstants.MERCHANT_SUMMARY_COUNT_TOTAL_COUNT_TYPE);
      merchantCountsSummaryDto.setCounts(originInfo.getDocCount());
      merchantCountsSummaryDto.setOrigin(originInfo.getKeyAsString());
      merchantCountsSummaryDto.setType(DataConstants.MERCHANT_SUMMARY_COUNT_TOTAL_COUNT_TYPE);
      merchantAggregationInfo.add(merchantCountsSummaryDto);
    }
    logger.info("MerchantDataSearch Response Transformed to MerchantCountsSummaryDto Successfully");
    return merchantAggregationInfo;
  }
}