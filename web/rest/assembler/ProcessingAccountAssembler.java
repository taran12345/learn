// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.web.rest.assembler;

import com.paysafe.mpp.commons.dto.ProcessingAccountDto;
import com.paysafe.mpp.commons.mastermerchant.resources.ProcessingAccountResource;
import com.paysafe.mpp.commons.util.AssemblerUtils;

import org.springframework.stereotype.Component;

/**
 * This is an assembler class for ProcessingAccountService.
 * 
 * @author satishmukku
 *
 */
@Component
public class ProcessingAccountAssembler {

  /**
   * This method converts ProcessingAccount to ProcessingAccountResource.
   * 
   * @param processingAccountDto
   *          ProcessingAccountDto
   * @return MasterPaymentAccountResource
   */

  public ProcessingAccountResource
      getProcessingAccountResource(ProcessingAccountDto processingAccountDto) {
    final ProcessingAccountResource processingAccountResource = new ProcessingAccountResource();
    AssemblerUtils.copyProperties(processingAccountResource, processingAccountDto);
    return processingAccountResource;
  }
}
