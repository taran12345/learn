// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE
/**
 * @author pratyushagarwal
 *
 */

package com.paysafe.mastermerchant.config;

import static springfox.documentation.builders.PathSelectors.regex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Predicate;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@EnableAutoConfiguration
public class SwaggerConfiguration {

  private static final Logger logger = LoggerFactory.getLogger(SwaggerConfiguration.class);

  private ApiInfo apiInfo() {

    return new ApiInfoBuilder().title("Master Merchant (MPP) API")
        .description("Master Merchant service will consolidate merchant data and serve it for OLAP use cases")
        .contact(new Contact("Team Arka", "", "Team-Arka@paysafe.com"))
        .version("v1")
        .build();
  }

  /**
   * MasterMerchant microservice Api.
   *
   * @return Docker.
   */
  @Bean
  public Docket masterMerchantApi() {
    logger.info("Configuring Swagger");
    return new Docket(DocumentationType.SWAGGER_2).groupName("masterMerchant-api").apiInfo(apiInfo()).select()
        .paths(cardConfigurationPaths()).build();
  }

  private Predicate<String> cardConfigurationPaths() {
    return regex(".*/mastermerchant/v1/.*");
  }
}
