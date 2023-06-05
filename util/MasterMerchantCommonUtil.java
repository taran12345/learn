// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.util;

import com.paysafe.mastermerchant.fileprocessing.util.FileProcessingConstants;
import com.paysafe.mastermerchant.fileprocessing.web.rest.resource.ProcessingFrequency;
import com.paysafe.mpp.commons.dto.MasterMerchantDto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

import lombok.extern.slf4j.Slf4j;

/**
 * Master merchant project common util.
 *
 * @author pranavrathi
 *
 */
@Slf4j
public final class MasterMerchantCommonUtil {

  private static final Gson gson = new Gson();

  private static final Logger logger = LoggerFactory.getLogger(MasterMerchantCommonUtil.class);

  private static final ObjectMapper mapper = new ObjectMapper();

  private MasterMerchantCommonUtil() {
  }

  public static MasterMerchantDto getDeepCopyForMasterMerchantDto(MasterMerchantDto masterMerchantDto) {
    return gson.fromJson(gson.toJson(masterMerchantDto), MasterMerchantDto.class);
  }

  public static <T> T deepCopy(T source, T destination) {
    return (T) gson.fromJson(gson.toJson(source), destination.getClass());
  }

  public static <T> T deepCopy(T source) {
    return (T) gson.fromJson(gson.toJson(source), source.getClass());
  }

  /**
   * Returns the same input resource with unicode unescaped.
   */
  public static <T> T copyWithUnicodeUnescaped(T source) {
    try {
      String content = mapper.writeValueAsString(source);
      content = StringEscapeUtils.unescapeJava(LogUtil.sanitizeInput(content));
      return (T) mapper.readValue(content, source.getClass());
    } catch (Exception ex) {
      log.error("Failed copyWithUnicodeUnescaped due to: {}", ex.getMessage(), ex);
    }
    return null;
  }

  /**
   * This utility parses the given string to integer and if it fails return null.
   */
  public static Integer parseIntFromStringOrReturnNull(String integerAsString) {
    int parsedInteger = 0;
    try {
      parsedInteger = Integer.parseInt(integerAsString);
    } catch (NumberFormatException e) {
      logger.error("Could not parse the given integer in string format", e);
    }
    return parsedInteger;
  }

  /**
   * This utility checks for null.
   */
  public static Object nullSafe(Object input) throws JsonProcessingException {
    if (input == null) {
      return null;
    }
    
    if (input instanceof String) {
      return mapper.writeValueAsString(StringEscapeUtils.escapeJava((String) input));
    }

    return input;
  }

  /**
   * This utility get boolean for input.
   */
  public static Object getBoolean(Object input) {
    if (input == null) {
      return null;
    }

    String inputString = ((String)input).toLowerCase(Locale.US);
    return FileProcessingConstants.BOOLEAN_CODE_ONE.equals(inputString)
            || FileProcessingConstants.BOOLEAN_CODE_TRUE.equals(inputString);
  }

  /**
   * Tells whether or not this file name matches the given date substring and regex.
   * @param subString substring to match in given fie name
   * @param fileRegEx reg expression to match in given file name
   * @return list of matched file names
   */
  public static boolean isFileNameMatches(String fileName, String subString, String fileRegEx) {
    return fileName.contains(subString) && fileName.matches(fileRegEx);
  }

  /**
   * Returns date time substring based on frequency of job.
   * @param dateTime given date time
   * @param processingFrequency job processing frequency
   * @return formatted date based on frequency
   */
  public static String getFormattedFileDate(String dateTime, ProcessingFrequency processingFrequency) {
    if (StringUtils.isNotBlank(dateTime) && dateTime.length() == 10
            && ProcessingFrequency.HOURLY.equals(processingFrequency)) {
      return dateTime.substring(0,10);
    } else {
      return dateTime.substring(0,8);
    }
  }

  /**
   * Remove encrypt format from file name.
   * @param encryptedFilesRegEx encrypted files regex
   * @return remove encrypt format from file name ex: abc.csv.pgp -> abc.csv
   */
  public static String getDecryptedFilesRegex(String encryptedFilesRegEx) {
    return FilenameUtils.removeExtension(encryptedFilesRegEx);
  }
}
