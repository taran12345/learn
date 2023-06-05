// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.service.impl;

import com.paysafe.mastermerchant.dataprocessing.utils.DataProcessingConstants;
import com.paysafe.mastermerchant.dataprocessing.utils.JsonTransformer;
import com.paysafe.mastermerchant.kafka.KafkaProducer;
import com.paysafe.mastermerchant.kafka.config.KafkaProducerTopics;
import com.paysafe.mastermerchant.service.GroupDataService;
import com.paysafe.mastermerchant.service.GroupSearchService;
import com.paysafe.mastermerchant.service.MasterMerchantDataService;
import com.paysafe.mastermerchant.service.PaymentAccountDataService;
import com.paysafe.mastermerchant.service.ProcessingAccountDataService;
import com.paysafe.mastermerchant.service.ReferenceMappingDataService;
import com.paysafe.mastermerchant.web.rest.assembler.MasterMerchantAssembler;
import com.paysafe.mpp.commons.dto.MasterMerchantDto;
import com.paysafe.mpp.commons.dto.MasterPaymentAccountDto;
import com.paysafe.mpp.commons.dto.ProcessingAccountDto;
import com.paysafe.mpp.commons.dto.SourceAuthority.Origin;
import com.paysafe.mpp.commons.enums.EntityType;
import com.paysafe.mpp.commons.enums.PaymentAccountType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class MasterMerchantDataServiceImpl implements MasterMerchantDataService {

  @Autowired
  private ReferenceMappingDataService referenceMappingService;

  @Autowired
  private KafkaProducer kafkaProducer;

  @Autowired
  private KafkaProducerTopics kafkaProducerTopics;

  @Autowired
  private GroupSearchService groupSearchService;

  @Autowired
  private MasterMerchantAssembler masterMerchantAssembler;

  @Autowired
  private GroupDataService groupDataService;

  @Autowired
  private PaymentAccountDataService paymentDataService;

  @Autowired
  private ProcessingAccountDataService processingDataService;

  private static final Logger logger = LoggerFactory.getLogger(MasterMerchantDataService.class);

  /**
   * This method saves MasterMerchant account in both oracle and elasticSearch.
   */
  public boolean saveMasterMerchantAccount(MasterMerchantDto masterMerchantDto) {

    try {
      MasterMerchantDto groupAccount = JsonTransformer.deepCopy(masterMerchantDto);
      MasterPaymentAccountDto paymentAccount = groupAccount.getPaymentAccounts().get(0);
      ProcessingAccountDto processingAccount = paymentAccount.getProcessingAccounts().get(0);

      groupAccount.setPaymentAccounts(null);
      paymentAccount.setProcessingAccounts(null);

      updateUuidValues(groupAccount, paymentAccount, processingAccount);

      // saves data in mapr
      groupDataService.encryptAndSaveToOracle(JsonTransformer.deepCopy(groupAccount));

      paymentDataService.encryptAndSaveToOracle(JsonTransformer.deepCopy(paymentAccount));

      processingDataService.encryptAndSaveToOracle(JsonTransformer.deepCopy(processingAccount));

      // Create single masterMerchant Object and save in elastic
      masterMerchantAssembler.updateMasterMerchantDto(groupAccount, paymentAccount, processingAccount);
      groupSearchService.encryptAndSaveToElastic(JsonTransformer.deepCopy(groupAccount));

      kafkaProducer.sendMessage(kafkaProducerTopics.getMasterMerchantTopic().getTopicName(), groupAccount,
          DataProcessingConstants.MASTER_MERCHANT_DATA, groupAccount.getSourceAuthority().getReferenceId());

      return true;
    } catch (Exception exc) {
      logger.error("Netbanx Data load Service - Exception occurred while processing merchant account", exc);
      return false;
    }
  }

  private void updateUuidValues(MasterMerchantDto groupAccount, MasterPaymentAccountDto paymentAccount,
      ProcessingAccountDto processingAccount) {

    String referenceId = groupAccount.getSourceAuthority().getReferenceId();
    Origin origin = Origin.valueOf(groupAccount.getSourceAuthority().getOrigin());
    PaymentAccountType accountType = PaymentAccountType.valueOf(groupAccount.getType().toString());

    String groupId = referenceMappingService.getOrCreateUuidForReferenceMapping(origin, EntityType.GROUP,
        accountType, referenceId);
    String paymentAccountId = referenceMappingService.getOrCreateUuidForReferenceMapping(origin,
        EntityType.PAYMENT_ACCOUNT, accountType, referenceId);
    String processingAccountId = referenceMappingService.getOrCreateUuidForReferenceMapping(origin,
        EntityType.PROCESSING_ACCOUNT, accountType, referenceId);

    processingAccount.setId(processingAccountId);
    processingAccount.setPaymentAccountId(paymentAccountId);
    processingAccount.setGroupId(groupId);

    paymentAccount.setId(paymentAccountId);
    paymentAccount.setGroupId(groupId);
    paymentAccount.setProcessingAccountIds(Arrays.asList(processingAccountId));

    groupAccount.setId(groupId);
    groupAccount.setPaymentAccountIds(Arrays.asList(paymentAccountId));
  }
}
