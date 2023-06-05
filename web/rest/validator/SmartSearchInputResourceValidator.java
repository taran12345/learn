// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.web.rest.validator;

import com.paysafe.mastermerchant.util.DataConstants;
import com.paysafe.op.errorhandling.exceptions.BadRequestException;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * SmartSearchInputResourceValidator.java.
 * 
 *
 * 
 */
public final class SmartSearchInputResourceValidator {

  private static final Pattern smartSearchValidationPattern = Pattern.compile(DataConstants.SMARTSEARCH_REGEX);

  private SmartSearchInputResourceValidator() {
  }

  /**
   * checks if the query params are in permitted range.
   *
   * 
   */
  public static void validateSearchRange(int offset, int limit) {
    if (offset + limit > DataConstants.MAX_RESULTS_FOR_SMARTSEARCH) {
      throw BadRequestException.builder().unsupportedOperation()
          .details("Cannot search in more than the permitted range of records. please refine your query").build();
    }
  }

  /**
   * checks if the number of records requested are in permitted range.
   * 
   *
   * 
   */
  public static void validateLimit(int limit) {
    if (limit > DataConstants.MAX_LIMIT_FOR_SMARTSEARCH) {
      throw BadRequestException.builder().unsupportedOperation()
          .details("Cannot fetch more than the permitted records. please refine your query").build();
    }
  }

  /**
   * Checks if the search query is valid.
   * @param searchQuery String
   */
  public static void validateSearchQuery(String searchQuery) {
    if (StringUtils.isBlank(searchQuery)) {
      throw BadRequestException.builder().unsupportedOperation()
          .details("MerchantDataSearch query cannot be empty or null").build();
    }

    if (searchQuery.trim().length() < DataConstants.MIN_LENGTH_FOR_SEARCHQUERY) {
      throw BadRequestException.builder().unsupportedOperation()
          .details(String.format("Length of search query cannot be less than %d",
              DataConstants.MIN_LENGTH_FOR_SEARCHQUERY)).build();
    }

    if (!smartSearchValidationPattern.matcher(searchQuery).matches()) {
      throw BadRequestException.builder().unsupportedOperation()
          .details(
              String.format("MerchantDataSearch query must contain atleast one Alphabet or Number"))
          .build();
    }
  }
}
