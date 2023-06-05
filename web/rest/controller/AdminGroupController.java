// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.web.rest.controller;

import com.paysafe.mastermerchant.service.GroupSearchService;
import com.paysafe.mastermerchant.service.MasterMerchantDataService;
import com.paysafe.mastermerchant.service.dto.GroupSearchRequestDto;
import com.paysafe.mastermerchant.service.dto.MasterMerchantResponseDto;
import com.paysafe.mastermerchant.util.ReferenceMappingValidationUtil;
import com.paysafe.mastermerchant.web.rest.assembler.GroupSearchAssembler;
import com.paysafe.mastermerchant.web.rest.assembler.MasterMerchantAssembler;
import com.paysafe.mastermerchant.web.rest.resource.GroupSearchRequestResource;
import com.paysafe.mastermerchant.web.rest.resource.MasterMerchantRecordCreateRequestResource;
import com.paysafe.mastermerchant.web.rest.resource.MasterMerchantRecordCreateResponseResource;
import com.paysafe.mastermerchant.web.rest.resource.MasterMerchantResponseResource;
import com.paysafe.mastermerchant.web.rest.validator.MasterMerchantResourceValidator;
import com.paysafe.mastermerchant.web.rest.validator.SearchInputResourceValidator;
import com.paysafe.mastermerchant.web.rest.validator.SmartSearchInputResourceValidator;
import com.paysafe.mpp.commons.dto.MasterMerchantDto;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.io.IOException;

import javax.validation.Valid;

/**
 * This is the controller which exposes apis to get MasterMerchant data.
 *
 * @author udaykiran
 */

@RestController
@RequestMapping(value = { "admin/mastermerchant/v1" })
public class AdminGroupController {

  @Autowired
  private MasterMerchantAssembler masterMerchantAssembler;

  @Autowired
  private GroupSearchAssembler groupSearchAssembler;

  @Autowired
  private GroupSearchService groupSearchService;

  @Autowired
  private ReferenceMappingValidationUtil referenceMappingValidationUtil;

  @Autowired
  private MasterMerchantDataService masterMerchantDataService;

  /**
   * Searches group objects.
   *
   * @param requestResource requestResource.
   * @return group search results.
   */
  @HystrixCommand(groupKey = "masterMerchant", commandKey = "masterMerchant", threadPoolKey = "masterMerchant")
  @RequestMapping(method = RequestMethod.POST, value = "/groups/search",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiOperation(value = "MerchantDataSearch for group records - Advanced MerchantDataSearch")
  @ApiResponses(value = { @ApiResponse(code = 404, message = "Not found") })
  public ResponseEntity<MasterMerchantResponseResource>
      search(@ApiParam(value = "Input JSON that takes group details  returns matching groups ",
          required = true) @Valid @RequestBody GroupSearchRequestResource requestResource) throws IOException {

    SearchInputResourceValidator.validateDateRanges(requestResource);

    GroupSearchRequestDto searchDto = groupSearchAssembler.getGroupSearchDto(requestResource);

    MasterMerchantResponseDto masterMerchantResponseDto = groupSearchService.findSimilarGroupDto(searchDto);

    return new ResponseEntity<>(masterMerchantAssembler.getMasterMerchantResponseResource(masterMerchantResponseDto),
        HttpStatus.OK);
  }

  /**
   * Smart search among groups.
   *
   * @param searchQuery that needs to be searched
   * @param offset offset of the documents.
   * @param limit number of documents to be returned.
   * @return search responses.
   */
  @HystrixCommand(groupKey = "masterMerchant", commandKey = "masterMerchant", threadPoolKey = "masterMerchant")
  @RequestMapping(method = RequestMethod.GET, value = "/groups/smartsearch/{searchQuery}",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiOperation(value = "get a mastermerchant record")
  @ApiResponses(value = { @ApiResponse(code = 404, message = "Not found") })
  public ResponseEntity<MasterMerchantResponseResource> smartSearch(
      @ApiParam(value = "the search query which needs to be searched",
          required = true) @PathVariable("searchQuery") String searchQuery,
      @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
      @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit) throws IOException {

    SmartSearchInputResourceValidator.validateSearchQuery(searchQuery);
    SmartSearchInputResourceValidator.validateSearchRange(offset, limit);
    SmartSearchInputResourceValidator.validateLimit(limit);

    MasterMerchantResponseDto masterMerchantResponseDto = groupSearchService.smartSearch(searchQuery, offset, limit);

    return new ResponseEntity<>(masterMerchantAssembler.getMasterMerchantResponseResource(masterMerchantResponseDto),
        HttpStatus.OK);
  }

  /**
   * Creates master merchant record.
   *
   * @param requestResource request resource
   * @return whether record is fully created or not
   * @throws InterruptedException when delay in returning response
   */
  @HystrixCommand(groupKey = "createMasterMerchantRecord",
      commandKey = "createMasterMerchantRecord", threadPoolKey = "createMasterMerchantRecord")
  @RequestMapping(method = RequestMethod.POST, value = "/groups", produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiOperation(value = "Create master merchant record")
  @ApiResponses(value = { @ApiResponse(code = 404, message = "Not found") })
  public ResponseEntity<MasterMerchantRecordCreateResponseResource>
      createMasterMerchantRecord(@ApiParam(value = "Input Master merchant create request resource",
          required = true) @Valid @RequestBody MasterMerchantRecordCreateRequestResource requestResource)
          throws InterruptedException {

    MasterMerchantResourceValidator.validateMasterMerchantRecordCreateRequestResource(requestResource);

    MasterMerchantDto masterMerchantDto = masterMerchantAssembler.getMasterMerchantDto(requestResource);

    referenceMappingValidationUtil.validateReferenceMappingAlreadyPresent(masterMerchantDto);

    boolean isRecordFullyCreated = masterMerchantDataService.saveMasterMerchantAccount(masterMerchantDto);

    Thread.sleep(1000);

    return new ResponseEntity<>(
        MasterMerchantRecordCreateResponseResource.builder().isRecordFullyCreated(isRecordFullyCreated).build(),
        HttpStatus.OK);
  }

}
