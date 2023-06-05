// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.web.rest.controller;

import com.paysafe.mastermerchant.service.PaymentAccountService;
import com.paysafe.mastermerchant.util.LogUtil;
import com.paysafe.mastermerchant.web.rest.assembler.PaymentAccountAssembler;
import com.paysafe.mpp.commons.dto.MasterPaymentAccountDto;
import com.paysafe.mpp.commons.mastermerchant.resources.PaymentAccountResource;
import com.paysafe.op.errorhandling.exceptions.NotFoundException;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * This is the controller which exposes apis to get MasterMerchant data.
 * 
 * @author pranayhiwase
 */

@RestController
@RequestMapping("admin/mastermerchant/v1")
public class AdminPaymentAccountController {

  private final PaymentAccountService masterPaymentAccountService;

  private final PaymentAccountAssembler masterPaymentAccountAssembler;

  /**
   * Constructor.
   */
  @Autowired
  public AdminPaymentAccountController(PaymentAccountService masterPaymentAccountService,
      PaymentAccountAssembler masterPaymentAccountAssembler) {
    this.masterPaymentAccountService = masterPaymentAccountService;
    this.masterPaymentAccountAssembler = masterPaymentAccountAssembler;
  }

  /**
   * This api gets complete PaymentAccountResource.
   * 
   * @param paymentAccountIdInput masterMerchantId
   * @return MasterPaymentAccountResource
   * @throws Exception exception
   */
  @HystrixCommand(groupKey = "getAccountCalls", commandKey = "getAccountCalls",
      threadPoolKey = "getAccountCalls")
  @RequestMapping(method = RequestMethod.GET, value = "paymentaccounts/{paymentAccountId}",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiOperation(value = "get a payment account record based on payment account id")
  @ApiResponses(value = { @ApiResponse(code = 404, message = "Not found") })
  public ResponseEntity<PaymentAccountResource> getMasterMerchantPaymentAccount(
      @ApiParam(value = "the id of the paymentaccount record",
          required = true) @PathVariable("paymentAccountId") String paymentAccountIdInput)
      throws Exception {

    String paymentAccountId = LogUtil.sanitizeInput(paymentAccountIdInput);
    MasterPaymentAccountDto responseDto =
        masterPaymentAccountService.getPaymentAccount(paymentAccountId);
    if (responseDto == null) {
      throw NotFoundException.builder().entityNotFound()
          .details("payment account with id " + paymentAccountId + " is not found").build();
    }
    return new ResponseEntity<>(
        masterPaymentAccountAssembler.getPaymentAccountResource(responseDto), HttpStatus.OK);
  }

}