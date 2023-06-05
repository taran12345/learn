// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.service.impl;

import com.paysafe.mastermerchant.service.GroupDocumentConverter;
import com.paysafe.mastermerchant.util.DataConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * DateFormatConverter.java.
 *
 * @author kamarapuprabhath
 *
 *
 */

@Service
public class DateFormatConverter implements GroupDocumentConverter {

  private static final Logger logger = LoggerFactory.getLogger(DateFormatConverter.class);

  private static final String PAYMENT_ACCOUNTS_LITERAL = "paymentAccounts";
  private static final String CREATION_DATE_LITERAL = "creationDate";
  private static final String PROCESSING_ACCOUNT_LITERAL = "processingAccounts";

  @Override
  public void convert(Map<String, Object> groupObject) {
    if (!groupObject.containsKey(PAYMENT_ACCOUNTS_LITERAL) || groupObject.get(PAYMENT_ACCOUNTS_LITERAL) == null) {
      return;
    }
    List<Map<String, Object>> paymentAccounts =
        (List<Map<String, Object>>) groupObject.get(PAYMENT_ACCOUNTS_LITERAL);

    for (Map<String, Object> paymentAccount : paymentAccounts) {
      if (paymentAccount != null) {
        populateCreationDateEs(paymentAccount);
      }
    }
  }

  private void populateCreationDateEs(Map<String, Object> paymentAccount) {
    if (paymentAccount.containsKey(CREATION_DATE_LITERAL)) {
      paymentAccount.put("creationDateEs", getFormattedDate(paymentAccount.get(CREATION_DATE_LITERAL)));
      logger.info("Populated creationDateEs for paymentAccount");
    }
    if (!paymentAccount.containsKey(PROCESSING_ACCOUNT_LITERAL)
        || paymentAccount.get(PROCESSING_ACCOUNT_LITERAL) == null) {
      return;
    }
    List<Map<String, Object>> processingAccounts =
        (List<Map<String, Object>>) paymentAccount.get(PROCESSING_ACCOUNT_LITERAL);
    for (Map<String, Object> processingAccount : processingAccounts) {
      if (processingAccount != null && processingAccount.containsKey(CREATION_DATE_LITERAL)) {
        processingAccount.put("creationDateEs", getFormattedDate(processingAccount.get(CREATION_DATE_LITERAL)));
        logger.info("Populated creationDateEs for processingAccount");
      }
    }
  }

  private String getFormattedDate(Object object) {
    if (object instanceof Map) {
      Map mapObject = ((Map) object);

      if (!mapObject.containsKey(DataConstants.DAY) || !mapObject.containsKey(DataConstants.MONTH)
          || !mapObject.containsKey(DataConstants.YEAR)) {
        return null;
      }

      if (mapObject.get(DataConstants.DAY) == null || mapObject.get(DataConstants.MONTH) == null
          || mapObject.get(DataConstants.YEAR) == null) {
        return null;
      }

      return mapObject.get(DataConstants.DAY) + "-" + mapObject.get(DataConstants.MONTH) + "-"
          + mapObject.get(DataConstants.YEAR);
    }
    return null;
  }

}
