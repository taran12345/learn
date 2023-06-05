// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.kafka.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * Kafka Topics.
 *
 * @author sravyakolli
 *
 */
@EnableConfigurationProperties
@Component
@Data
@ConfigurationProperties(prefix = "kafka-producer-topics")
public class KafkaProducerTopics {
  private Topic groupTopic;
  private Topic paymentAccountTopic;
  private Topic processingAccountTopic;  
  private Topic masterMerchantTopic;
  private Topic merchantIdsTopic;
  private Topic denormalizedMerchantsTopic;

  @Data
  public static class Topic {
    private String topicName;
  }
}