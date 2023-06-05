// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.validation.web.rest.controller;

import com.paysafe.mastermerchant.util.DataConstants;
import com.paysafe.mastermerchant.validation.assembler.ValidationAssembler;
import com.paysafe.mastermerchant.validation.dto.SearchTemplateRequestDto;
import com.paysafe.mastermerchant.validation.enums.OracleView;
import com.paysafe.mastermerchant.validation.repository.model.OracleResponse;
import com.paysafe.mastermerchant.validation.service.impl.ElasticSearchValidationServiceImpl;
import com.paysafe.mastermerchant.validation.service.impl.OracleValidationServiceImpl;
import com.paysafe.mastermerchant.validation.web.rest.resource.SearchTemplateRequestResource;
import com.paysafe.ss.permission.client.annotations.HasOnePermission;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import org.elasticsearch.action.search.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
import java.util.Map;

import javax.validation.Valid;

@RestController
public class StoredTemplateController {

  @Autowired
  private OracleValidationServiceImpl oracleValidationServiceImpl;

  @Autowired
  private ElasticSearchValidationServiceImpl elasticSearchValidationServiceImpl;

  /**
   * Gets response of given oracle view.
   *
   * @return oracle view response.
   * @throws Exception when view is not present
   */
  @HystrixCommand(groupKey = "validationCalls", commandKey = "validationCalls", threadPoolKey = "validationCalls")
  @GetMapping(value = "mastermerchant/v1/storedTemplates/oracleValidation/{oracleViewName}",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiOperation(value = "get oracle view response")
  @ApiResponses(value = {@ApiResponse(code = 404, message = "Not found")})
  @HasOnePermission(DataConstants.CONFIGURE_PERMISSION)
  public ResponseEntity<List<OracleResponse>> getOracleViewResponse(
      @ApiParam(value = "Authorization access token", required = true) @RequestHeader("Authorization") String auth,
      @ApiParam(value = "name of oracle database view",
          required = true) @PathVariable("oracleViewName") OracleView oracleViewName)
      throws Exception {

    return new ResponseEntity<>(oracleValidationServiceImpl.searchOracleView(oracleViewName), HttpStatus.OK);
  }

  /**
   * Gets response of given oracle view Admin.
   *
   * @return oracle view response.
   * @throws Exception when view is not present
   */
  @HystrixCommand(groupKey = "validationCalls", commandKey = "validationCalls", threadPoolKey = "validationCalls")
  @RequestMapping(method = RequestMethod.GET,
      value = "admin/mastermerchant/v1/storedTemplates/oracleValidation/{oracleViewName}",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiOperation(value = "get oracle view response")
  @ApiResponses(value = {@ApiResponse(code = 404, message = "Not found")})
  public ResponseEntity<List<OracleResponse>> adminGetOracleViewResponse(
      @ApiParam(value = "name of oracle database view",
          required = true) @PathVariable("oracleViewName") OracleView oracleViewName)
      throws Exception {

    return new ResponseEntity<>(oracleValidationServiceImpl.searchOracleView(oracleViewName), HttpStatus.OK);
  }

  /**
   * Gets response of given elasticsearch search template.
   *
   * @return search response.
   * @throws Exception when template is not present
   */
  @HystrixCommand(groupKey = "validationCalls", commandKey = "validationCalls", threadPoolKey = "validationCalls")
  @PostMapping(value = "mastermerchant/v1/storedTemplates/elasticValidation",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiOperation(value = "get elastic searchTemplate response")
  @ApiResponses(value = {@ApiResponse(code = 404, message = "Not found")})
  @HasOnePermission(DataConstants.CONFIGURE_PERMISSION)
  public ResponseEntity<Map<String, Object>> getSearchTemplateResponse(
      @ApiParam(value = "Authorization access token", required = true) @RequestHeader("Authorization") String auth,
      @ApiParam(value = "Input JSON that takes group details returns matching groups ",
          required = true) @Valid @RequestBody SearchTemplateRequestResource inputResource)
      throws Exception {

    return new ResponseEntity<>(processElasticSearchStoredTemplates(inputResource), HttpStatus.OK);
  }

  /**
   * Gets response of given elasticsearch search template Admin.
   *
   * @return search response.
   * @throws Exception when template is not present
   */
  @HystrixCommand(groupKey = "validationCalls", commandKey = "validationCalls", threadPoolKey = "validationCalls")
  @PostMapping(value = "admin/mastermerchant/v1/storedTemplates/elasticValidation",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiOperation(value = "get elastic searchTemplate response")
  @ApiResponses(value = {@ApiResponse(code = 404, message = "Not found")})
  public ResponseEntity<Map<String, Object>> adminGetSearchTemplateResponse(
      @ApiParam(value = "Input JSON that takes group details  returns matching groups ",
          required = true) @Valid @RequestBody SearchTemplateRequestResource inputResource)
      throws Exception {

    return new ResponseEntity<>(processElasticSearchStoredTemplates(inputResource), HttpStatus.OK);
  }


  /**
   * Gets response from elasticsearch.
   *
   * @return search response.
   * @throws Exception When es throws an Exception
   */
  private Map<String, Object> processElasticSearchStoredTemplates(SearchTemplateRequestResource inputResource)
      throws Exception {
    SearchTemplateRequestDto inputDto = ValidationAssembler.getSearchTemplateRequestDto(inputResource);

    SearchResponse elasticSearchResponse = elasticSearchValidationServiceImpl.searchScript(inputDto);

    return ValidationAssembler.getSearchTemplateRequestResponse(elasticSearchResponse);
  }
}
