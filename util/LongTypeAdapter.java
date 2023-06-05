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
 * LongtypeAdapter.java.
 * 
 * @author kamarapuprabhath
 *
 * 
 */

public class LongTypeAdapter extends TypeAdapter<Long> {

  private static final Logger logger = LoggerFactory.getLogger(LongTypeAdapter.class);

  @Override
  public Long read(JsonReader reader) throws IOException {
    if (reader.peek() == JsonToken.NULL) {
      reader.nextNull();
      return null;
    }
    String stringValue = reader.nextString();
    try {
      if (!StringUtils.isEmpty(stringValue)) {
        return (long) Double.parseDouble(stringValue);
      }
    } catch (NumberFormatException e) {
      logger.error("Failed to convert string to long ", e);
    }
    logger.error("Failed to convert string to long {}", stringValue);
    return null;
  }

  @Override
  public void write(JsonWriter writer, Long value) throws IOException {
    if (value == null) {
      writer.nullValue();
      return;
    }
    writer.value(value);
  }
}
