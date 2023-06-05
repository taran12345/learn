// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * DoubleTypeAdapter.java.
 * 
 * @author kamarapuprabhath
 *
 * 
 */

public class DoubleTypeAdapter extends TypeAdapter<Double> {

  private static final Logger logger = LoggerFactory.getLogger(DoubleTypeAdapter.class);

  @Override
  public Double read(JsonReader reader) throws IOException {
    if (reader.peek() == JsonToken.NULL) {
      reader.nextNull();
      return null;
    }
    String stringValue = reader.nextString();
    if (!StringUtils.isEmpty(stringValue)) {

      stringValue = stringValue.replace("\\+", "");
      try {
        return Double.valueOf(stringValue);
      } catch (NumberFormatException e) {
        logger.error("Failed to convert string to double ", e);
      }
    }
    logger.error("Failed to convert string to double {}", stringValue);
    return null;
  }

  @Override
  public void write(JsonWriter writer, Double value) throws IOException {
    if (value == null) {
      writer.nullValue();
      return;
    }
    writer.value(value);
  }

}
