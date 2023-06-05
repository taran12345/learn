// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.service.impl;

import com.paysafe.mastermerchant.repository.GroupSearchIndexRepository;
import com.paysafe.mastermerchant.service.ProcessingAccountService;
import com.paysafe.mastermerchant.util.DataConstants;
import com.paysafe.mastermerchant.util.LogUtil;
import com.paysafe.mpp.commons.dto.MasterMerchantDto;
import com.paysafe.mpp.commons.dto.ProcessingAccountDto;
import com.paysafe.mpp.commons.service.CryptoService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * ProcessingAccountServiceImpl.java.
 *
 * @author satishmukku
 */
@Service
public class ProcessingAccountServiceImpl implements ProcessingAccountService {

  private static final Logger logger = LoggerFactory.getLogger(ProcessingAccountServiceImpl.class);

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  private CryptoService cryptoService;

  @Autowired
  private GroupSearchIndexRepository groupSearchIndexRepository;

  /**
   * This service returns the processingAccount.
   *
   * @param processingAccountIdInput processingAccountId
   * @return ProcessingAccountDto
   * @throws Exception exception
   */
  @Override
  public ProcessingAccountDto getProcessingAccount(String processingAccountIdInput) throws Exception {
    logger.info("UUID search, fetching processingAccounts from Elastic");
    String processingAccountId = LogUtil.sanitizeInput(processingAccountIdInput);
    Map<String, Object> parameters = new HashMap<>();
    parameters.put(DataConstants.PROCESSING_ACCOUNT_ID, processingAccountId);

    return getDecryptProcessingAccount(parameters, DataConstants.PROCESSING_ACCOUNT_UUID_SEARCH_TEMPLATE);
  }


  /**
   * This service returns the processingAccount based on referenceId.
   *
   * @param referenceId referenceId
   * @param source source
   * @return ProcessingAccountDto
   * @throws Exception exception
   */
  @Override
  public ProcessingAccountDto getProcessingAccount(String referenceId, String source, String type) throws Exception {
    logger.info("ReferecnceId search, Fetching processingAccounts from Elastic");

    Map<String, Object> parameters = new HashMap<>();
    parameters.put(DataConstants.ORIGIN, source);
    parameters.put(DataConstants.REFERENCE_ID, referenceId);
    parameters.put(DataConstants.TYPE, type);

    return getDecryptProcessingAccount(parameters, DataConstants.PROCESSING_ACCOUNT_REF_ID_SEARCH_TEMPLATE);
  }

  /**
   * This service decrypts and returns the processingAccount.
   *
   * @param params params
   * @param searchTemplateName template
   * @return ProcessingAccountDto
   * @throws Exception exception
   */
  private ProcessingAccountDto getDecryptProcessingAccount(Map<String, Object> params, String searchTemplateName)
      throws Exception {
    ProcessingAccountDto processingAccountDto =
        getProcessingAccountFromElastic(params, searchTemplateName);

    if (processingAccountDto == null) {
      return null;
    }

    // Nullyfying revenueAgreementData
    processingAccountDto.setRevenueAgreement(null);
    processingAccountDto = (ProcessingAccountDto) cryptoService.decrypt(processingAccountDto);
    return processingAccountDto;
  }

  /**
   * This service decrypts and returns the processingAccount.
   *
   * @param params params
   * @param searchTemplateName template
   * @return ProcessingAccountDto
   * @throws Exception exception
   */
  private ProcessingAccountDto getProcessingAccountFromElastic(Map<String, Object> params, String searchTemplateName)
      throws Exception {
    SearchResponse response = groupSearchIndexRepository.searchScript(params, searchTemplateName);

    if (response.getHits().getTotalHits() > 0) {
      String result = response.getHits().getAt(0).getSourceAsString();
      MasterMerchantDto masterMerchant = objectMapper.readValue(result, MasterMerchantDto.class);
      return masterMerchant.getPaymentAccounts().get(0).getProcessingAccounts().get(0);
    } else {
      return null;
    }
  }

  /**
   * This method gets data from elasticSearch.
   *
   */
  @Override
  public List<String> getProcessingAccountIds(String pmleId) throws IOException, Exception {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("pmleId", pmleId);

    return getProcessingAccountIdsFromElastic(parameters, DataConstants.PMLE_SEARCH_TEMPLATE);
  }

  /**
   * Returns a list of processingAccounts associated to a pmleid from elastic.
   *
   * @throws Exception exception
   */
  private List<String> getProcessingAccountIdsFromElastic(Map<String, Object> params, String searchTemplateName)
      throws Exception {
    SearchResponse response = groupSearchIndexRepository.searchScript(params, searchTemplateName);

    List<String> merchantsIds = new ArrayList<>();
    for (SearchHit searchResult : response.getHits()) {
      String searchResultJson = searchResult.getSourceAsString();
      MasterMerchantDto masterMerchant = objectMapper.readValue(searchResultJson, MasterMerchantDto.class);
      String uuid = masterMerchant.getPaymentAccounts().get(0).getProcessingAccounts().get(0).getId();
      merchantsIds.add(uuid);
    }
    return merchantsIds;
  }

}
