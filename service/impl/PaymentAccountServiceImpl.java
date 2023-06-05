// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.service.impl;

import com.paysafe.mastermerchant.repository.GroupSearchIndexRepository;
import com.paysafe.mastermerchant.service.PaymentAccountService;
import com.paysafe.mastermerchant.util.DataConstants;
import com.paysafe.mastermerchant.util.LogUtil;
import com.paysafe.mpp.commons.dto.MasterMerchantDto;
import com.paysafe.mpp.commons.dto.MasterPaymentAccountDto;
import com.paysafe.mpp.commons.service.CryptoService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * PaymentAccountServiceImpl.java.
 *
 * @author satishmukku
 */
@Service
public class PaymentAccountServiceImpl implements PaymentAccountService {

  private static final Logger logger = LoggerFactory.getLogger(PaymentAccountServiceImpl.class);

  @Autowired
  private CryptoService cryptoService;

  @Autowired
  private GroupSearchIndexRepository groupSearchIndexRepository;

  private final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * This service returns the MasterPaymentAccount.
   *
   * @param paymentAccountIdInput paymentAccountId
   * @return MasterPaymentAccountDTO
   * @throws Exception exception
   */
  @Override
  public MasterPaymentAccountDto getPaymentAccount(String paymentAccountIdInput) throws Exception {
    String paymentAccountId = LogUtil.sanitizeInput(paymentAccountIdInput);
    logger.info("UUID search, fetching paymentAccounts from Elastic");

    Map<String, Object> parameters = new HashMap<>();
    parameters.put(DataConstants.PAYMENTACCOUNTID, paymentAccountId);

    MasterPaymentAccountDto paymentAccountDto =
        getPaymentAccountFromElastic(parameters, DataConstants.PAYMENT_ACCOUNT_UUID_SEARCH_TEMPLATE);

    if (paymentAccountDto == null) {
      return null;
    }

    paymentAccountDto = (MasterPaymentAccountDto) cryptoService.decrypt(paymentAccountDto);

    return paymentAccountDto;
  }

  private MasterPaymentAccountDto getPaymentAccountFromElastic(Map<String, Object> params, String searchTemplateName)
      throws Exception {
    SearchResponse response = groupSearchIndexRepository.searchScript(params, searchTemplateName);

    if (response.getHits().getTotalHits() > 0) {
      String result = response.getHits().getAt(0).getSourceAsString();
      MasterMerchantDto masterMerchant = objectMapper.readValue(result, MasterMerchantDto.class);
      return masterMerchant.getPaymentAccounts().get(0);
    } else {
      return null;
    }
  }
}
