// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.merchantsearch.web.rest.controller;

import com.paysafe.mastermerchant.merchantsearch.assembler.MerchantSearchAssembler;
import com.paysafe.mastermerchant.merchantsearch.dto.MerchantSearchDownloadRequestDto;
import com.paysafe.mastermerchant.merchantsearch.dto.MerchantSearchRequestDto;
import com.paysafe.mastermerchant.merchantsearch.dto.MerchantSearchResponseDto;
import com.paysafe.mastermerchant.merchantsearch.service.MerchantSearchService;
import com.paysafe.mastermerchant.merchantsearch.web.rest.resource.MerchantSearchDownloadRequestResource;
import com.paysafe.mastermerchant.merchantsearch.web.rest.resource.MerchantSearchRequestResource;
import com.paysafe.mastermerchant.merchantsearch.web.rest.resource.MerchantSearchResponseResource;
import com.paysafe.mastermerchant.merchantsearch.web.rest.validator.MerchantSearchRequestValidator;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
public class MerchantSearchController {

  @Autowired
  private MerchantSearchAssembler merchantSearchAssembler;

  @Autowired
  private MerchantSearchService merchantSearchService;

  @Autowired
  private MerchantSearchRequestValidator merchantSearchRequestValidator;

  /**
   * Searches merchant data.
   *
   *
   * @param requestResource requestResource.
   * @return response results.
   * @throws Exception - Elastic to MM model parsing exception
   */
  @HystrixCommand(groupKey = "merchantSearch", commandKey = "merchantSearch", threadPoolKey = "merchantSearch")
  @RequestMapping(method = RequestMethod.POST, value = "admin/mastermerchant/v1/merchantsearch/search",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiOperation(value = "MerchantSearch for account - contains and exact match")
  @ApiResponses(value = {@ApiResponse(code = 404, message = "Not found")})
  public ResponseEntity<MerchantSearchResponseResource> search(
      @ApiParam(value = "Input JSON with account search details",
          required = true) @Valid @RequestBody MerchantSearchRequestResource requestResource)
      throws Exception {

    return searchMerchantData(requestResource);
  }

  /**
   * Searches merchant account data.
   *
   * @param requestResource AccountSearchRequestResource.
   * @return response results.
   * @throws Exception - Elastic to MM model parsing exception
   */
  @HystrixCommand(groupKey = "merchantSearch", commandKey = "merchantSearch", threadPoolKey = "merchantSearch")
  @RequestMapping(method = RequestMethod.POST, value = "mastermerchant/v1/merchantsearch/search",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiOperation(value = "MerchantSearch for account - contains and exact match")
  @ApiResponses(value = {@ApiResponse(code = 404, message = "Not found")})
  public ResponseEntity<MerchantSearchResponseResource> search(
      @ApiParam(value = "Authorization access token", required = true) @RequestHeader("Authorization") String auth,
      @ApiParam(value = "Input JSON with account search details",
          required = true) @Valid @RequestBody MerchantSearchRequestResource requestResource)
      throws Exception {

    return searchMerchantData(requestResource);
  }

  /**
   * Searches merchant data.
   *
   *
   * @param requestResource requestResource.
   * @return response results.
   * @throws Exception - Elastic to MM model parsing exception
   */
  @RequestMapping(method = RequestMethod.POST, value = "admin/mastermerchant/v1/merchantsearch/download",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiOperation(value = "MerchantSearch for account - contains and exact match")
  @ApiResponses(value = {@ApiResponse(code = 404, message = "Not found")})
  public ResponseEntity downloadMerchantSearchResults(
      @ApiParam(value = "Input JSON with account search details",
          required = true) @Valid @RequestBody MerchantSearchDownloadRequestResource requestResource,
      HttpServletResponse servletResponse) throws Exception {

    return downloadMerchantData(requestResource, servletResponse);
  }

  /**
   * Searches merchant account data.
   *
   * @param requestResource AccountSearchRequestResource.
   * @return response results.
   * @throws Exception - Elastic to MM model parsing exception
   */
  @RequestMapping(method = RequestMethod.POST, value = "mastermerchant/v1/merchantsearch/download",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiOperation(value = "MerchantSearch for account - contains and exact match")
  @ApiResponses(value = {@ApiResponse(code = 404, message = "Not found")})
  public ResponseEntity downloadMerchantSearchResults(
      @ApiParam(value = "Authorization access token", required = true) @RequestHeader("Authorization") String auth,
      @ApiParam(value = "Input JSON with account search details",
          required = true) @Valid @RequestBody MerchantSearchDownloadRequestResource requestResource,
      HttpServletResponse servletResponse) throws Exception {

    return downloadMerchantData(requestResource, servletResponse);
  }

  private ResponseEntity<MerchantSearchResponseResource> searchMerchantData(
      MerchantSearchRequestResource requestResource) throws Exception {

    // TODO remove this after validation refactoring
    merchantSearchRequestValidator.validateMerchantSearchRequestResource(requestResource);

    MerchantSearchRequestDto searchDto = merchantSearchAssembler.getMerchantSearchRequestDto(requestResource);

    merchantSearchRequestValidator.validateMerchantSearchRequestDto(searchDto);

    MerchantSearchResponseDto responseDto = merchantSearchService.processMerchantSearchRequest(searchDto);

    return new ResponseEntity<>(merchantSearchAssembler.getMerchantSearchResponseResource(responseDto), HttpStatus.OK);
  }

  private ResponseEntity downloadMerchantData(MerchantSearchDownloadRequestResource requestResource,
      HttpServletResponse servletResponse) throws Exception {

    MerchantSearchDownloadRequestDto searchDto =
        merchantSearchAssembler.getMerchantSearchDownloadRequestDto(requestResource);

    merchantSearchRequestValidator.validateMerchantSearchDownloadRequestDto(searchDto);

    MerchantSearchResponseDto responseDto = merchantSearchService.processMerchantSearchRequest(searchDto);

    List<String[]> csvData = merchantSearchAssembler.getMerchantDataInDownloadableFormat(searchDto, responseDto);
    merchantSearchService.writeSearchResponseToOutputStream(csvData, servletResponse);

    return ResponseEntity.status(HttpStatus.OK).build();
  }
}
