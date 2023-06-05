// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.kafka;

import com.paysafe.mastermerchant.util.LogUtil;
import com.paysafe.op.errorhandling.exceptions.InternalErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Master Merchant Producer.
 *
 * @author pranavrathi
 *
 */
@Component
public class KafkaProducer {

  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;

  private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);
  private final ObjectMapper mapper = new ObjectMapper();

  /**
   * Send message to a topic.
   * @param topic name
   * @param message to be sent
   * @param recordTypeInput to display in log
   * @param idInput to display in log
   */
  @Async(value = "asyncExecutor")
  public void sendMessage(String topic, Object message, String recordTypeInput, String idInput) {
    String recordType = LogUtil.sanitizeInput(recordTypeInput);
    String id = LogUtil.sanitizeInput(idInput);
    try {
      kafkaTemplate.send(topic, mapper.writeValueAsString(message));
      logger.info("Data for record type {} with id {} published successfully to topic: {}", recordType, id, topic);
    } catch (Exception exc) {
      logger.error("Couldn't push message with record type {} and id {} to topic {}. Exception is {}", recordType, id,
          topic, exc.getMessage(), exc);
      throw InternalErrorException.builder().cause(exc)
          .detail("Exception while pushing message with record type {} and id {} to topic {}. Exception is {}",
              recordType, id, topic, exc)
          .build();
    }
  }

  /**
   * Send message to a topic.
   *
   * @param topic of the message.
   * @param message to be sent to consumer.
   */
  public void sendMessage(String topic, String message) {
    try {
      kafkaTemplate.send(topic, message);
      logger.info(" Sending message from producer  Topic: {}  Message : {}", topic, message);
    } catch (Exception exc) {
      logger.error("Couldn't push message to topic {}. Exception is {}", topic, exc.getMessage(), exc);
      throw InternalErrorException.builder().cause(exc)
          .detail("Exception while pushing message to topic {}. Exception is {}", topic, exc)
          .build();
    }

  }

}
