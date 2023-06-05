// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.web.rest.controller;

import com.paysafe.mastermerchant.service.ProcessingAccountService;
import com.paysafe.mastermerchant.util.LogUtil;
import com.paysafe.mastermerchant.web.rest.assembler.ProcessingAccountAssembler;
import com.paysafe.mpp.commons.dto.ProcessingAccountDto;
import com.paysafe.mpp.commons.mastermerchant.resources.ProcessingAccountResource;
import com.paysafe.op.errorhandling.CommonErrorCode;
import com.paysafe.op.errorhandling.exceptions.BadRequestException;
import com.paysafe.op.errorhandling.exceptions.NotFoundException;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.List;

/**
 * This is the controller which exposes apis to get ProcessingAccount data.
 *
 * @author satish
 */

@RestController
@RequestMapping("admin/mastermerchant/v1")
public class AdminProcessingAccountController {

  private final ProcessingAccountService processingAccountService;

  private final ProcessingAccountAssembler processingAccountAssembler;

  /**
   * Constructor.
   */
  @Autowired
  public AdminProcessingAccountController(ProcessingAccountService processingAccountService,
      ProcessingAccountAssembler processingAccountAssembler) {
    this.processingAccountService = processingAccountService;
    this.processingAccountAssembler = processingAccountAssembler;
  }

  /**
   * This api returns ProcessingAccount for a given id.
   *
   * @param processingAccountIdInput processingAccountId
   * @return ProcessingAccountResource
   * @throws Exception exception
   */
  @HystrixCommand(groupKey = "getAccountCalls", commandKey = "getAccountCalls",
      threadPoolKey = "getAccountCalls")
  @RequestMapping(method = RequestMethod.GET, value = "processingAccounts/{processingAccountId}",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiOperation(value = "get processing account record based on Id")
  @ApiResponses(value = {@ApiResponse(code = 404, message = "Not found")})
  public ResponseEntity<ProcessingAccountResource> getProcessingAccount(
      @ApiParam(value = "the id of the processing account record",
          required = true) @PathVariable("processingAccountId") String processingAccountIdInput)
      throws Exception {
    String processingAccountId = LogUtil.sanitizeInput(processingAccountIdInput);
    ProcessingAccountDto responseDto =
        processingAccountService.getProcessingAccount(processingAccountId);
    if (responseDto == null) {
      throw new NotFoundException.Builder()
          .details("Failed to fetch processing account with id : " + processingAccountId)
          .errorCode(CommonErrorCode.ENTITY_NOT_FOUND).build();
    }

    return new ResponseEntity<>(
        processingAccountAssembler.getProcessingAccountResource(responseDto), HttpStatus.OK);
  }

  /**
   * This api returns ProcessingAccount for a given sourceId.
   *
   * @param source source
   * @param sourceId sourceId
   * @param type type
   * @return ProcessingAccount
   * @throws Exception exception
   */
  @HystrixCommand(groupKey = "getProcessingAccountBySource",
      commandKey = "getProcessingAccountBySource", threadPoolKey = "getProcessingAccountBySource")
  @RequestMapping(method = RequestMethod.GET, value = "/processingAccounts",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiOperation(value = "get a procesingAccount")
  @ApiResponses(value = {@ApiResponse(code = 404, message = "Not found")})
  public ResponseEntity<ProcessingAccountResource> getProcessingAccountInternal(
      @ApiParam(value = "source for which search needs to be done",
          required = true) @RequestParam(value = "source") String source,
      @ApiParam(value = "source id of the processingAccount record",
          required = true) @RequestParam("sourceId") String sourceId,
      @ApiParam(value = "type of the processingAccount for which search needs to be done",
          required = true) @RequestParam(value = "type") String type)
      throws Exception {
    ProcessingAccountDto responseDto =
        processingAccountService.getProcessingAccount(sourceId, source, type);

    if (responseDto == null) {
      throw NotFoundException.builder().entityNotFound().details("Processing Account with id "
          + sourceId + " source = " + source + " type  " + type + " is not found").build();
    }
    return new ResponseEntity<>(
        processingAccountAssembler.getProcessingAccountResource(responseDto), HttpStatus.OK);
  }

  /**
   * This api returns ProcessingAccount for a given sourceId.
   *
   * @param pmleId pmleId
   * @return processingAccountIds
   * @throws Exception exception
   */
  @HystrixCommand(groupKey = "getProcessingAccountIdsByPmle",
      commandKey = "getProcessingAccountIdsByPmle", threadPoolKey = "getProcessingAccountIdsByPmle")
  @RequestMapping(method = RequestMethod.GET, value = "/processingAccountIds",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiOperation(value = "get procesingAccountIds based on pmleId")
  @ApiResponses(value = {@ApiResponse(code = 404, message = "Not found")})
  public ResponseEntity<List<String>> getProcessingAccountIdsByPmle(
      @ApiParam(value = "source for which search needs to be done",
          required = true) @RequestParam(value = "pmleId") String pmleId)
      throws Exception {
    if (StringUtils.isEmpty(pmleId)) {
      throw BadRequestException.builder().details("pmleId cannot be null or empty").build();
    }
    List<String> processingAccountIds = processingAccountService.getProcessingAccountIds(pmleId);
    if (CollectionUtils.isEmpty(processingAccountIds)) {
      throw NotFoundException.builder().entityNotFound()
          .details(String.format("No processing account found for pmleId : %s ", pmleId)).build();
    }
    return new ResponseEntity<>(processingAccountIds, HttpStatus.OK);
  }

}