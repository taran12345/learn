// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.web.rest.controller;

import com.paysafe.mastermerchant.service.MerchantAccountService;
import com.paysafe.mastermerchant.service.PmleService;
import com.paysafe.mastermerchant.service.dto.MerchantAccountDto;
import com.paysafe.mastermerchant.service.dto.PmleDto;
import com.paysafe.mastermerchant.util.DataConstants;
import com.paysafe.mastermerchant.web.rest.assembler.NbxMerchantAccountAssembler;
import com.paysafe.mastermerchant.web.rest.assembler.PmleAssembler;
import com.paysafe.mastermerchant.web.rest.resource.NbxAccountBasicDetailsResource;
import com.paysafe.mastermerchant.web.rest.resource.PmleResponse;
import com.paysafe.op.errorhandling.CommonErrorCode;
import com.paysafe.op.errorhandling.exceptions.BadRequestException;
import com.paysafe.ss.permission.client.annotations.HasOnePermission;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.constraints.NotEmpty;

/**
 * This is the controller which exposes APIs to get Netbanx account data.
 *
 * @author nareshpentkar
 */
@RestController
@RequestMapping({"admin/mastermerchant/v1", "mastermerchant/v1"})
@HasOnePermission(DataConstants.VIEW_PERMISSION)
public class NbxDataController {

  @Autowired
  private NbxMerchantAccountAssembler nbxMerchantAccountAssembler;

  @Autowired
  private MerchantAccountService merchantAccountService;

  @Autowired
  private PmleAssembler pmleAssembler;

  @Autowired
  private PmleService pmleService;

  /**
   * Fetch account basic details given Netbanx accountId.
   *
   * @param auth authorization token
   * @param accountId netbanx account id
   * @return instance of {@link NbxAccountBasicDetailsResource} 
   */
  @HystrixCommand(groupKey = "masterMerchant", commandKey = "masterMerchant", threadPoolKey = "masterMerchant")
  @GetMapping(value = "/nbx/merchants/{accountId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiOperation(value = "Fetch account basic details given a Netbanx Account Id")
  @ApiResponses(value = {@ApiResponse(code = 404, message = "Not found")})
  public ResponseEntity<NbxAccountBasicDetailsResource> fetchNbxAccount(
      @ApiParam(value = "Authorization access token", required = false) @RequestHeader("Authorization") String auth,
      @ApiParam(value = "NBX Account id", required = true) @PathVariable("accountId") String accountId) {
    MerchantAccountDto dto = merchantAccountService.findByAccountId(accountId);
    NbxAccountBasicDetailsResource resource = nbxMerchantAccountAssembler.dtoToResource(dto);
    return new ResponseEntity<>(resource, HttpStatus.OK);
  }

  /**
   * Fetches the ID of a PMLE when its name is given.
   *
   * @param name Name of the PMLE whose ID is required
   * @return {@link PmleResponse}, which carries the end response
   */
  @GetMapping(value = "/nbx/pmles", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiOperation(value = "Fetches PMLE ID on passing the merchant's name")
  public ResponseEntity<PmleResponse> fetchPmleId(
      @ApiParam(value = "Authorization Token") @RequestHeader("Authorization") String auth,
      @ApiParam @NotEmpty @RequestParam(value = "name", required = true) String name) {

    if (StringUtils.isEmpty(name)) {
      throw BadRequestException.builder().errorCode(CommonErrorCode.INVALID_FIELD)
          .details("Merchant PMLE name should not be empty").build();
    }
    PmleDto dto = pmleService.getPmleIdByName(name);
    PmleResponse response = pmleAssembler.responseFromDto(dto);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
  


}
