// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.web.rest.validator;

import com.paysafe.mastermerchant.web.rest.resource.GroupSearchRequestResource;
import com.paysafe.op.errorhandling.exceptions.BadRequestException;

import java.time.LocalDateTime;

/**
 * SearchInputResourceValidator.java.
 */
public final class SearchInputResourceValidator {

  private SearchInputResourceValidator() {
  }

  /**
   * checks if the date range is valid.
   */
  public static void validateDateRanges(GroupSearchRequestResource requestResource) {
    LocalDateTime processingAccountCreationStartDate = requestResource.getProcessingAccountCreationStartDate();
    LocalDateTime processingAccountCreationEndDate = requestResource.getProcessingAccountCreationEndDate();

    if (processingAccountCreationStartDate == null ^ processingAccountCreationEndDate == null) {
      throw BadRequestException.builder().unsupportedOperation()
          .details("you have to provide both processingAccountCreationStartDate and processingAccountCreationEndDate")
          .build();
    }

    if (processingAccountCreationStartDate != null
        && processingAccountCreationStartDate.isAfter(processingAccountCreationEndDate)) {
      throw BadRequestException.builder().unsupportedOperation()
          .details("processingAccountCreationEndDate should be after processingAccountCreationStartDate").build();
    }
  }
}
