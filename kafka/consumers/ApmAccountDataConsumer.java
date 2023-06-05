// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.kafka.consumers;

import com.paysafe.mastermerchant.dataprocessing.resource.ApmAccountDataResource;
import com.paysafe.mastermerchant.dataprocessing.service.ApmAccountDataService;
import com.paysafe.mastermerchant.kafka.config.KafkaConsumerTopics;
import com.paysafe.mastermerchant.util.DataConstants;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@ConditionalOnProperty(prefix = "apm", value = "enabled", havingValue = "true")
public class ApmAccountDataConsumer {

  private static final ObjectMapper mapper = new ObjectMapper();

  @Autowired
  private KafkaConsumerTopics kafkaConsumerTopics;

  @Autowired
  private ApmAccountDataService apmAccountDataService;

  /**
   * Consumes messages from apm account data Topic.
   *
   * @throws Exception exception
   */
  @KafkaListener(topics = "#{kafkaConsumerTopics.getApmDataTopic().getTopicName().split(',')}",
      groupId = "#{kafkaConsumerTopics.getApmDataTopic().getGroupId()}",
      containerFactory = DataConstants.APM_DATA_CONTAINER_FACTORY)
  protected void processRecord(final ConsumerRecord<?, String> message) throws Exception {
    log.info("Apm account data record is consumed with message offset:{}", message.offset());
    ApmAccountDataResource apmAccountDataResource = mapper.readValue(message.value(), ApmAccountDataResource.class);
    String correlationId = getCorrelationId((message));
    log.info("correlation: {}", correlationId);
    String accountId = correctExternalAccountId(apmAccountDataResource);
    if (StringUtils.isBlank(accountId)) {
      log.error("No FMA id found for APM Account data: {}", message.value());
      return;
    }
    if (!accountId.matches("\\d+")) {
      log.error("Invalid FMA {} received with correlation:{} from APM kafka:{}",
          accountId, correlationId, message.value());
      return;
    }
    try {
      apmAccountDataService.processApmAccountData(apmAccountDataResource);
      log.info("Apm account data processed for FMA:{} with correlation:{}", accountId, correlationId);
    } catch (Exception ex) {
      log.error("Failed processing APM account data for fma:{} with correlation:{} due to:{}",
          accountId, correlationId, ex.getMessage(), ex);
    }
  }

  private String correctExternalAccountId(ApmAccountDataResource apmAccountDataResource) {
    String accountId = apmAccountDataResource.getExternalAccountId();
    if (StringUtils.isNotBlank(accountId) && accountId.contains("_")) {
      accountId = accountId.split("_")[0];
      apmAccountDataResource.setExternalAccountId(accountId);
    }
    return apmAccountDataResource.getExternalAccountId();
  }

  private String getCorrelationId(ConsumerRecord<?, String> message) {
    if (message.headers() == null) {
      return null;
    }
    return java.util.stream.Stream.of(message.headers().toArray())
        .filter(header -> StringUtils.equals(header.key(), "correlationId"))
        .map(header -> new String(header.value())).findFirst().orElse(null);
  }

}
