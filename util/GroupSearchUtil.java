// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.util;

import com.paysafe.mastermerchant.config.Mapping;
import com.paysafe.mpp.commons.dto.DateFormat;
import com.paysafe.mpp.commons.dto.MasterMerchantDto;
import com.paysafe.mpp.commons.dto.MasterPaymentAccountDto;
import com.paysafe.mpp.commons.dto.ProcessingAccountDto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * GroupSearchUtil.java.
 * 
 * @author kamarapuprabhath
 *
 * 
 */

public final class GroupSearchUtil {

  private static final Logger logger = LoggerFactory.getLogger(GroupSearchUtil.class);

  private static final ObjectMapper objectMapper = new ObjectMapper();

  private GroupSearchUtil() {

  }

  /**
   * GetDate as string.
   * 
   * @param creationDate DateFormat.
   * @return date as a string.
   */
  public static String getDate(DateFormat creationDate) {
    if (creationDate == null) {
      return null;
    }
    StringBuilder date = new StringBuilder();
    date.append(creationDate.getDay() == null ? "00" : creationDate.getDay());
    date.append("-");
    date.append(creationDate.getMonth() == null ? "00" : creationDate.getMonth());
    date.append("-");
    date.append(creationDate.getYear() == null ? "0000" : creationDate.getYear());
    return date.toString();
  }

  /**
   * getValue returns String or ArrayList.
   * 
   * @param masterMerchant masterMerchantObject.
   * @param groupMapping mapping
   * @return value
   */
  public static Object getValue(MasterMerchantDto masterMerchant, Map.Entry<String, Mapping> groupMapping) {
    String jsonPath = groupMapping.getValue().getPath();
    String valueType = groupMapping.getValue().getType();
    Map<String, Object> map = objectMapper.convertValue(masterMerchant, Map.class);
    JSONObject jsonObject = new JSONObject(map);
    Object value = null;
    try {
      value = JsonPath.read(jsonObject, jsonPath);
      if (value != null && "STRING".equals(valueType) && value instanceof JSONArray) {
        value = ((JSONArray) value).length() == 0 ? null : ((JSONArray) value).get(0);
      } else if (value != null && "LIST".equals(valueType) && value instanceof String) {
        value = Arrays.asList(value);
      }
      if (isEmptyString(value)) {
        return null;
      }
      if (value instanceof JSONArray) {
        return getList((JSONArray) value);
      }
      return value;
    } catch (Exception ex) {
      logger.info("Could not read value at path ", ex);
      return null;
    }
  }

  /**
   * isEmptyString.
   * 
   * @param value object
   * @return true if its empty String or List with emptyStrings.
   */
  public static boolean isEmptyString(Object value) {
    if (value instanceof String && StringUtils.isBlank((String) value)) {
      logger.debug("Found Empty String");
      return true;
    } else if (value instanceof List && ((List) value).size() == 1
        && StringUtils.isBlank((String) (((List) value).get(0)))) {
      return true;
    }
    return false;
  }

  /**
   * get list of Strings from jsonArray.
   * 
   * @param jsonArray jsonArray
   * @return list of stings.
   */
  public static List<String> getList(JSONArray jsonArray) {
    List<String> listdata = new ArrayList<>();
    if (jsonArray != null) {
      for (int i = 0; i < jsonArray.length(); i++) {
        try {
          listdata.add(jsonArray.getString(i));
        } catch (JSONException ex) {
          logger.info("Failed to get String from json array", ex);
        }
      }
    }
    return listdata;
  }

  /**
   * Checks if payment account exists in a group.
   * 
   * @param masterMerchant masterMerchant
   * @return isPaymentAccountExists.
   */
  public static Boolean paymentAccountExists(MasterMerchantDto masterMerchant) {
    if (!CollectionUtils.isEmpty(masterMerchant.getPaymentAccounts())
        && masterMerchant.getPaymentAccounts().get(0) != null) {
      if (StringUtils.isEmpty(masterMerchant.getPaymentAccounts().get(0).getId())) {
        logger.error("PaymentAccountId not present in group with id {}", masterMerchant.getId());
        return false;
      } else {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks if processing account exists in a payment account.
   * 
   * @param paymentAccount paymentAccount
   * @return isProcessingAccountExists.
   */
  public static Boolean processingAccountExists(MasterPaymentAccountDto paymentAccount) {
    if (!CollectionUtils.isEmpty(paymentAccount.getProcessingAccounts())
        && paymentAccount.getProcessingAccounts().get(0) != null) {
      if (StringUtils.isEmpty(paymentAccount.getProcessingAccounts().get(0).getId())) {
        logger.error("ProcessingAccountId not present in paymentAccount with id {}", paymentAccount.getId());
        return false;
      } else {
        return true;
      }
    }
    return false;
  }

  /**
   * gets the first payment account id.
   * 
   * @param masterMerchant masterMerchant
   * @return payment account id.
   */
  public static String getFirstPaymentAccountId(MasterMerchantDto masterMerchant) {
    MasterPaymentAccountDto masterPaymentAccountDto = getFirstPaymentAccount(masterMerchant);
    return masterPaymentAccountDto == null ? null : masterPaymentAccountDto.getId();
  }

  /**
   * gets the first processing account id.
   * 
   * @param masterMerchant masterMerchant
   * @return processingAccoutId.
   */
  public static String getFirstProcessingAccountId(MasterMerchantDto masterMerchant) {
    ProcessingAccountDto processingAccountDto = getFirstProcessingAccount(masterMerchant);
    return processingAccountDto == null ? null : processingAccountDto.getId();
  }

  /**
   * gets the first payment account corresponding to master merchant.
   * 
   * @param masterMerchant masterMerchant
   * @return paymentAccount.
   */
  public static MasterPaymentAccountDto getFirstPaymentAccount(MasterMerchantDto masterMerchant) {
    return masterMerchant.getPaymentAccounts() == null ? null : masterMerchant.getPaymentAccounts().get(0);
  }

  /**
   * gets the First processing account corresponding to master merchant.
   * 
   * @param masterMerchant masterMerchant
   * @return ProcessingAccountDto.
   */
  private static ProcessingAccountDto getFirstProcessingAccount(MasterMerchantDto masterMerchant) {
    MasterPaymentAccountDto masterPaymentAccountDto = getFirstPaymentAccount(masterMerchant);
    if (masterPaymentAccountDto != null) {
      List<ProcessingAccountDto> processingAccounts = masterPaymentAccountDto.getProcessingAccounts();
      return CollectionUtils.isEmpty(processingAccounts) ? null : processingAccounts.get(0);
    }
    return null;
  }

}