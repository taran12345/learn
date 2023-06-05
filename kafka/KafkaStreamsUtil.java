// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.kafka;

import com.paysafe.mastermerchant.util.DoubleTypeAdapter;
import com.paysafe.mastermerchant.util.LongTypeAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.springframework.stereotype.Component;

/**
 * Util class for Kafka Streams.
 *
 * @author sravyakolli
 *
 */
@Component
public class KafkaStreamsUtil {

  private static final Gson gson = new GsonBuilder().registerTypeAdapter(Long.class, new LongTypeAdapter())
      .registerTypeAdapter(Double.class, new DoubleTypeAdapter()).create();

  /**
   * Parses the given consumer message to given class.
   * @param source Cosumer Message
   * @param destinationClass Class
   * @return Destination class object
   */
  public static <T> T parseJson(String source, Class destinationClass) {
    return (T) gson.fromJson(source, destinationClass);
  }
}