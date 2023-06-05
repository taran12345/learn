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
@ConfigurationProperties(prefix = "kafka-consumer-topics")
public class KafkaConsumerTopics {
  private Topic groupTopic;
  private Topic paymentAccountTopic;
  private Topic processingAccountTopic;  
  private Topic masterMerchantTopic;
  private Topic rs2DataTopic;
  private Topic apmDataTopic;

  @Data
  public static class Topic {
    private String topicName;
    private String groupId;
  }
}