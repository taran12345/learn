// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.kafka.consumers;

import com.paysafe.mastermerchant.kafka.KafkaStreamsUtil;
import com.paysafe.mastermerchant.kafka.config.KafkaConsumerTopics;
import com.paysafe.mastermerchant.service.GroupSearchService;
import com.paysafe.mastermerchant.util.DataConstants;
import com.paysafe.mpp.commons.dto.MasterMerchantDto;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * MasterMerchantDataConsumerService.java.
 *
 * @author pranayhiwase
 *
 */
@Service
public class MasterMerchantDataConsumerServiceImpl {

  private static final Logger logger = LoggerFactory.getLogger(MasterMerchantDataConsumerServiceImpl.class);

  @Autowired
  private KafkaConsumerTopics kafkaConsumerTopics;

  @Autowired
  private KafkaStreamsUtil kafkaStreamsUtil;

  @Autowired
  private GroupSearchService groupSearchService;

  private static final ObjectMapper mapper = new ObjectMapper();

  public KafkaStreamsUtil getKafkaStreamsUtil() {
    return this.kafkaStreamsUtil;
  }

  public KafkaConsumerTopics getKafkaConsumerTopics() {
    return this.kafkaConsumerTopics;
  }

  /**
   * Consumes messages from MasterMerchant Topic.
   * @param message ConsumerRecord Message from MasterMerchant Topic
   */
  @KafkaListener(topics = "#{kafkaConsumerTopics.getMasterMerchantTopic().getTopicName().split(',')}",
      groupId = "#{kafkaConsumerTopics.getMasterMerchantTopic().getGroupId()}",
      containerFactory = DataConstants.MASTER_MERCHANT_CONTAINER_FACTORY)
  protected void processRecord(final ConsumerRecord<?, String> message) throws IOException {
    logger.info("MasterMerchantData record is consumed from topic {}", message.topic());

    MasterMerchantDto masterMerchant = mapper.readValue(message.value(), MasterMerchantDto.class);
    groupSearchService.encryptAndSaveToElastic(masterMerchant);
  }

}