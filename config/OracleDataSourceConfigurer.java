// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.config;

import com.paysafe.op.errorhandling.exceptions.InternalErrorException;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;

import oracle.jdbc.pool.OracleConnectionPoolDataSource;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.Map.Entry;
import java.util.Properties;

@Component
public class OracleDataSourceConfigurer implements BeanPostProcessor {

  @Autowired
  private OracleConnectionProperties oracleConnectionProperties;

  private static final Logger logger = LoggerFactory.getLogger(OracleDataSourceConfigurer.class);

  @Override
  public Object postProcessBeforeInitialization(Object bean, String name) throws BeansException {
    if (bean instanceof HikariDataSource && MapUtils.isNotEmpty(oracleConnectionProperties.getConnectionProperties())) {
      HikariDataSource hikariDataSource = (HikariDataSource) bean;
      if (hikariDataSource.getHikariPoolMXBean() instanceof HikariPool) {
        HikariPool hikariPool = (HikariPool) hikariDataSource.getHikariPoolMXBean();
        if (hikariPool.getUnwrappedDataSource() instanceof OracleConnectionPoolDataSource) {
          OracleConnectionPoolDataSource oracleConnectionPoolDataSource =
              (OracleConnectionPoolDataSource) hikariPool.getUnwrappedDataSource();
          addConnectionPropertiesToDataSource(oracleConnectionPoolDataSource);
        }
      }
    }
    return bean;
  }

  private void addConnectionPropertiesToDataSource(OracleConnectionPoolDataSource oracleConnectionPoolDataSource) {
    try {
      Properties connectionProperties;
      connectionProperties = oracleConnectionPoolDataSource.getConnectionProperties();
      for (Entry<String, String> connectionProperty : oracleConnectionProperties.getConnectionProperties().entrySet()) {
        connectionProperties.setProperty(connectionProperty.getKey(), connectionProperty.getValue());
      }
      oracleConnectionPoolDataSource.setConnectionProperties(connectionProperties);
    } catch (Exception exc) {
      logger.error("Error while updating data source properties - {}", exc.getMessage());
      throw InternalErrorException.builder().cause(exc).detail("Failed to update oracle data source.").build();
    }
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String name) throws BeansException {
    return bean;
  }
}