// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.kafka.consumers;

import com.paysafe.mastermerchant.kafka.config.KafkaConsumerTopics;
import com.paysafe.mastermerchant.rs2.dto.Rs2ConsumerDataDto;
import com.paysafe.mastermerchant.rs2.service.Rs2DataProcessingService;
import com.paysafe.mastermerchant.util.DataConstants;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "rs2", value = "enabled", havingValue = "true")
public class Rs2DataConsumer {

  private static final Logger logger = LoggerFactory.getLogger(Rs2DataConsumer.class);

  @Autowired
  private KafkaConsumerTopics kafkaConsumerTopics;

  @Autowired
  private Rs2DataProcessingService rs2DataProcessingService;

  private static final ObjectMapper mapper = new ObjectMapper();

  public KafkaConsumerTopics getKafkaConsumerTopics() {
    return this.kafkaConsumerTopics;
  }

  /**
   * Consumes messages from rs2Data Topic.
   * 
   * @throws Exception exception
   */
  @KafkaListener(topics = "#{kafkaConsumerTopics.getRs2DataTopic().getTopicName().split(',')}",
      groupId = "#{kafkaConsumerTopics.getRs2DataTopic().getGroupId()}",
      containerFactory = DataConstants.RS2_DATA_CONTAINER_FACTORY)
  protected void processRecord(final ConsumerRecord<?, String> message) throws Exception {
    logger.info("RS2 record is consumed from topic {}", message.topic());
    Rs2ConsumerDataDto rs2ConsumerDataDto = mapper.readValue(message.value(), Rs2ConsumerDataDto.class);
    rs2DataProcessingService.processRs2ConsumerData(rs2ConsumerDataDto);
  }
}
