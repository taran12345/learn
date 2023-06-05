// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.web.rest.controller;

import com.paysafe.mastermerchant.service.SearchService;
import com.paysafe.mastermerchant.service.dto.SearchResponseDto;
import com.paysafe.mastermerchant.util.DataConstants;
import com.paysafe.mastermerchant.web.rest.assembler.SearchAssembler;
import com.paysafe.mastermerchant.web.rest.resource.SearchRequestResource;
import com.paysafe.mastermerchant.web.rest.resource.SearchResponseResource;
import com.paysafe.mastermerchant.web.rest.resource.TemplateMetadataRequestResource;
import com.paysafe.ss.permission.client.annotations.HasOnePermission;

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

import javax.validation.Valid;

/**
 * This is the controller which exposes apis to search or facilitate searching MasterMerchant data.
 * 
 * @author abhineetagarwal
 */

@RestController
@RequestMapping(value = {"mastermerchant/v1"})
@HasOnePermission(DataConstants.VIEW_PERMISSION)
public class SearchController {

  @Autowired
  private SearchAssembler searchAssembler;

  @Autowired
  private SearchService searchService;
  

  /**
   * This api is for searching based on given template and dynamic field values associated with it.
   * 
   * @param requestResource template search request resource containing details about template name and parameter
   *          values.
   * @return templateSearchResponseResource
   */
  @HystrixCommand(groupKey = "masterMerchant", commandKey = "masterMerchant", threadPoolKey = "masterMerchant")
  @RequestMapping(method = RequestMethod.POST, value = "/search", produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiOperation(value = "MerchantDataSearch for records based on template name provided")
  @ApiResponses(value = {@ApiResponse(code = 404, message = "Not found")})
  public ResponseEntity<SearchResponseResource> templateSearch(
      @ApiParam(value = "Authorization access token", required = true) @RequestHeader("Authorization") String auth,
      @ApiParam(value = "Input JSON that takes template search request details",
          required = true) @Valid @RequestBody SearchRequestResource requestResource) {
    SearchResponseDto searchResponse = searchService.getRecords(searchAssembler.getSearchDto(requestResource));
    return new ResponseEntity<>(searchAssembler.getSearchResource(searchResponse), HttpStatus.OK);
  }

  @HystrixCommand(groupKey = "masterMerchant", commandKey = "masterMerchant", threadPoolKey = "masterMerchant")
  @RequestMapping(method = RequestMethod.POST, value = "/save/searchTemplate",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiOperation(value = "Save search templates")
  @ApiResponses(value = {@ApiResponse(code = 404, message = "Not found")})
  @HasOnePermission(DataConstants.CONFIGURE_PERMISSION)
  public ResponseEntity<Void> saveSearchTemplate(
      @ApiParam(value = "Authorization access token", required = true) @RequestHeader("Authorization") String auth,
      @ApiParam(value = "Input JSON that will be stored as template metadata",
          required = true) @Valid @RequestBody TemplateMetadataRequestResource metadataResource) {
    searchService.saveTemplate(searchAssembler.getTemplateMetadataDto(metadataResource));
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
