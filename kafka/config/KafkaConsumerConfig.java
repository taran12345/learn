// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.kafka.config;

import com.paysafe.mastermerchant.util.DataConstants;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka Consumer Configuration.
 *
 * @author sravyakolli
 *
 */
@Configuration
@EnableKafka
public class KafkaConsumerConfig {

  @Autowired
  private KafkaStreamsConfig kafkaStreamsConfig;

  @Autowired
  private KafkaConsumerTopics kafkaConsumerTopics;

  /**
   * Creates common properties for all consumers.
   *
   * @return props with ConsumerConfig properties
   */
  public Map<String, Object> consumerConfigs() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaStreamsConfig.getBootstrapServers());
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, DataConstants.KAFKA_AUTO_OFFSET_RESET);
    return props;
  }

  /**
   * Creates masterMerchant consumer factory.
   *
   * @return ConsumerFactory
   */
  @Bean
  public ConsumerFactory<String, String> masterMerchantConsumerFactory() {
    Map<String, Object> consumerConfigs = consumerConfigs();
    consumerConfigs.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerTopics.getMasterMerchantTopic().getGroupId());
    return new DefaultKafkaConsumerFactory<>(consumerConfigs());
  }

  /**
   * Creates KafkaListenerContainerFactory for MaterMerchant.
   *
   * @param masterMerchantConsumerFactory ConsumerFactory
   * @return KafkaListenerContainerFactory
   */
  @Bean
  public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>>
      kafkaListenerContainerFactory(
      @Autowired final ConsumerFactory<String, String> masterMerchantConsumerFactory) {
    ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(masterMerchantConsumerFactory);
    return factory;
  }

  /**
   * Creates rs2 consumer factory.
   *
   * @return ConsumerFactory
   */
  @Bean
  @ConditionalOnProperty(prefix = "rs2", value = "enabled", havingValue = "true")
  public ConsumerFactory<String, String> rs2DataConsumerFactory() {
    Map<String, Object> consumerConfigs = consumerConfigs();
    consumerConfigs.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerTopics.getRs2DataTopic().getGroupId());
    return new DefaultKafkaConsumerFactory<>(consumerConfigs());
  }

  /**
   * Creates KafkaListenerContainerFactory for Rs2Data.
   *
   * @param rs2DataConsumerFactory ConsumerFactory
   * @return KafkaListenerContainerFactory
   */
  @Bean
  @ConditionalOnProperty(prefix = "rs2", value = "enabled", havingValue = "true")
  public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> rs2DataContainerFactory(
      @Autowired final ConsumerFactory<String, String> rs2DataConsumerFactory) {
    ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(rs2DataConsumerFactory);
    return factory;
  }

  /**
   * Creates ApmAccountData consumer factory.
   *
   * @return ConsumerFactory
   */
  @Bean
  public ConsumerFactory<String, String> apmDataConsumerFactory() {
    Map<String, Object> consumerConfigs = consumerConfigs();
    consumerConfigs.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerTopics.getRs2DataTopic().getGroupId());
    return new DefaultKafkaConsumerFactory<>(consumerConfigs());
  }

  /**
   * Creates KafkaListenerContainerFactory for ApmAccountData.
   *
   * @param apmDataConsumerFactory ConsumerFactory
   * @return KafkaListenerContainerFactory
   */
  @Bean
  public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> apmDataContainerFactory(
      @Autowired final ConsumerFactory<String, String> apmDataConsumerFactory) {
    ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(apmDataConsumerFactory);
    return factory;
  }

  /**
   * Creates group consumer factory.
   *
   * @return ConsumerFactory
   */
  @Bean
  public ConsumerFactory<String, String> groupConsumerFactory() {
    Map<String, Object> consumerConfigs = consumerConfigs();
    consumerConfigs.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerTopics.getGroupTopic().getGroupId());
    return new DefaultKafkaConsumerFactory<>(consumerConfigs());
  }

  /**
   * Creates KafkaListenerContainerFactory for Group.
   *
   * @param groupConsumerFactory ConsumerFactory
   * @return KafkaListenerContainerFactory
   */
  @Bean
  public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>>
      groupContainerFactory(
      @Autowired final ConsumerFactory<String, String> groupConsumerFactory) {
    ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(groupConsumerFactory);
    return factory;
  }

  /**
   * Creates payment account consumer factory.
   *
   * @return ConsumerFactory
   */
  @Bean
  public ConsumerFactory<String, String> paymentAccountConsumerFactory() {
    Map<String, Object> consumerConfigs = consumerConfigs();
    consumerConfigs.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerTopics.getPaymentAccountTopic().getGroupId());
    return new DefaultKafkaConsumerFactory<>(consumerConfigs());
  }

  /**
   * Creates KafkaListenerContainerFactory for PaymentAccount.
   *
   * @param paymentAccountConsumerFactory ConsumerFactory
   * @return KafkaListenerContainerFactory
   */
  @Bean
  public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>>
      paymentAccountContainerFactory(
      @Autowired final ConsumerFactory<String, String> paymentAccountConsumerFactory) {
    ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(paymentAccountConsumerFactory);
    return factory;
  }

  /**
   * Creates processing account consumer factory.
   *
   * @return ConsumerFactory
   */
  @Bean
  public ConsumerFactory<String, String> processingAccountConsumerFactory() {
    Map<String, Object> consumerConfigs = consumerConfigs();
    consumerConfigs.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerTopics.getProcessingAccountTopic().getGroupId());
    return new DefaultKafkaConsumerFactory<>(consumerConfigs());
  }

  /**
   * Creates KafkaListenerContainerFactory for ProcessingAccount.
   *
   * @param processingAccountConsumerFactory ConsumerFactory
   * @return KafkaListenerContainerFactory
   */
  @Bean
  public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>>
      processingAccountContainerFactory(
      @Autowired final ConsumerFactory<String, String> processingAccountConsumerFactory) {
    ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(processingAccountConsumerFactory);
    return factory;
  }
}
