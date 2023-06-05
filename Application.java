// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant;

import com.paysafe.mastermerchant.fileprocessing.repository.jpa.BatchRepositoryImpl;
import com.paysafe.mastermerchant.util.DataConstants;
import com.paysafe.op.commons.framework.repository.EnableOneplatformDatabase;
import com.paysafe.op.commons.framework.security.vault.VaultAutoConfiguration;
import com.paysafe.op.commons.framework.springboot.StartupHelper;
import com.paysafe.op.errorhandling.EnableOneplatformErrorHandling;
import com.paysafe.ss.logging.EnableOneplatformTracing;
import com.paysafe.ss.permission.client.annotations.EnableOneplatformAuthorization;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JsonOrgJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JsonOrgMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import com.netflix.hystrix.contrib.javanica.aop.aspectj.HystrixCommandAspect;

import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import paysafe.ss.tokenization.hashing.TokenConfiguration;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.Executor;

/**
 * Master Merchant application.
 *
 * this application will be used to store entire mastermerchant data.
 *
 * @author udaykiran
 *
 */

@RefreshScope
@EnableDiscoveryClient
@EnableAsync
@EnableOneplatformErrorHandling
@SpringBootApplication
@EnableOneplatformAuthorization
@EnableOneplatformTracing
@EnableOneplatformDatabase
@EnableJpaRepositories(repositoryBaseClass = BatchRepositoryImpl.class)
@Import({VaultAutoConfiguration.class, TokenConfiguration.class})
@EnableAspectJAutoProxy
@EnableFeignClients
@ComponentScan(basePackages = {"com.paysafe.mastermerchant",
    "com.paysafe.mpp.commons", "com.paysafe.mastermerchant.repository",
    "com.paysafe.op.commons.framework.security.vault",
    "com.paysafe.op.file.storage", "com.paysafe.op.file.storage.config"})
public class Application implements CommandLineRunner {

  public static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

  /**
   * Initialize spring boot application.
   *
   * @param args main parameters
   */
  public static void main(String... args) throws IOException {
    TimeZone.setDefault(DateTimeZone.UTC.toTimeZone());
    StartupHelper.launch(Application.class, args, "Merchant Payment Processing - Master Merchant");
  }

  /**
   * Run Asynchronously.
   *
   * @param args arguments
   */
  @Override
  public void run(String... args) {
    setJaywayConfiguration();
  }

  private void setJaywayConfiguration() {
    Configuration.setDefaults(new Configuration.Defaults() {

      private final JsonProvider jsonProvider = new JsonOrgJsonProvider();
      private final MappingProvider mappingProvider = new JsonOrgMappingProvider();

      @Override
      public JsonProvider jsonProvider() {
        return jsonProvider;
      }

      @Override
      public MappingProvider mappingProvider() {
        return mappingProvider;
      }

      @Override
      public Set<Option> options() {
        return EnumSet.noneOf(Option.class);
      }
    });
  }

  /**
   * Thread executor kafka consumer.
   */
  @Bean(name = DataConstants.KAFKA_CONSUMER_EXECUTOR_NAME)
  public Executor getKafkaConsumerAsyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(DataConstants.KAFKA_CONSUMER_CORE_THREADPOOL_SIZE);
    executor.setMaxPoolSize(DataConstants.KAFKA_CONSUMER_MAX_THREADPOOL_SIZE);
    executor.setQueueCapacity(DataConstants.KAFKA_CONSUMER_THREAD_QUEUE_SIZE);
    executor.setThreadNamePrefix("KafkaConsumerService-");
    executor.initialize();
    return executor;
  }

  @Bean
  public HystrixCommandAspect hystrixAspect() {
    return new HystrixCommandAspect();
  }
}
