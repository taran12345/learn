// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.web.rest.controller;

import com.paysafe.mastermerchant.service.PmleService;
import com.paysafe.mastermerchant.service.dto.PmleDto;
import com.paysafe.mastermerchant.web.rest.assembler.PmleAssembler;
import com.paysafe.mastermerchant.web.rest.resource.PmleResponse;
import com.paysafe.op.errorhandling.CommonErrorCode;
import com.paysafe.op.errorhandling.exceptions.BadRequestException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.validation.constraints.NotEmpty;

/**
 * This is the controller which exposes APIs to get Netbanx account data.
 */
@RestController
@RequestMapping({"admin/mastermerchant/v1"})
public class NbxAdminDataController {

  @Autowired
  private PmleAssembler pmleAssembler;

  @Autowired
  private PmleService pmleService;

  /**
   * Fetches the PMLE when its id is given.
   *
   * @param id Id of the PMLE whose details is required
   * @return {@link PmleResponse}, which carries the end response
   */
  @GetMapping(value = "/nbx/pmles/{pmleId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiOperation(value = "Fetches PMLE on passing the PMLE Id")
  public ResponseEntity<PmleResponse> fetchPmle(@ApiParam @NotEmpty @PathVariable(value = "pmleId") Long id) {

    if (id == null) {
      throw BadRequestException.builder().errorCode(CommonErrorCode.INVALID_FIELD)
          .details("Merchant PMLE id should not be empty").build();
    }

    PmleDto dto = pmleService.getPmleById(id);
    PmleResponse response = pmleAssembler.responseFromDto(dto);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

}
