// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.kafka.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * Kafka Streams Configuration.
 *
 * @author sravyakolli
 *
 */
@EnableConfigurationProperties
@Component
@Data
@ConfigurationProperties(prefix = "kafka-streams-config")
public class KafkaStreamsConfig {
  private String bootstrapServers;
}