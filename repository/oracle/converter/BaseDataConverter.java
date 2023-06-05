// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.repository.oracle.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import javax.persistence.AttributeConverter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseDataConverter<T> implements AttributeConverter<T, String> {

  private static final ObjectMapper mapper = new ObjectMapper();

  public abstract Class<T> getInstance();

  @Override
  public String convertToDatabaseColumn(T attribute) {
    try {
      return mapper.writeValueAsString(attribute);
    } catch (JsonProcessingException ex) {
      log.error("Exception while converting to json.");
      return null;
    }
  }

  @Override
  public T convertToEntityAttribute(String dbData) {
    try {
      return mapper.readValue(dbData, this.getInstance());
    } catch (IOException ex) {
      log.error("Exception while decoding json from database: " + dbData);
      return null;
    }
  }
}