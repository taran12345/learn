// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.web.rest.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.paysafe.mastermerchant.service.MvelTemplateService;
import com.paysafe.mastermerchant.service.dto.MvelTemplateDto;
import com.paysafe.mastermerchant.service.dto.MvelTemplateResponseDto;
import com.paysafe.mastermerchant.util.DiscoveryServiceutil;
import com.paysafe.mastermerchant.util.LogUtil;
import com.paysafe.mastermerchant.web.rest.assembler.MvelTemplateAssembler;
import com.paysafe.mastermerchant.web.rest.resource.MvelTemplateResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiParam;

import java.net.UnknownHostException;

import javax.validation.Valid;

/**
 * MVEL templates controller.
 *
 * @author nareshpentkar
 */
@RestController
@RequestMapping({"admin/mastermerchant/v1/mvel-templates"})
public class AdminMvelTemplateController {

  @Autowired
  private MvelTemplateAssembler mvelTemplateAssembler;

  @Autowired
  private MvelTemplateService mvelTemplateService;

  @Autowired
  private DiscoveryServiceutil discoveryServiceutil;

  /**
   * API to get template base on templateName.
   */
  @RequestMapping(produces = APPLICATION_JSON_VALUE, method = RequestMethod.GET, value = "/{templateName}")
  public MvelTemplateResponse getByTemplateName(
      @ApiParam(value = "MVEL template name", required = true)
      @PathVariable("templateName") final String templateName) throws UnknownHostException {
    discoveryServiceutil.isCurrentServerNode(0);

    String sanitizedTemplateName = LogUtil.sanitizeInput(templateName);
    MvelTemplateResponseDto responseDto = mvelTemplateService.getMvelTemplateByName(sanitizedTemplateName);
    return mvelTemplateAssembler.getMvelTemplateResponse(responseDto);
  }

  /**
   * API to create or update MVEL template for a given template name.
   *
   * @return MVEL template response
   */
  @RequestMapping(method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
  public ResponseEntity<MvelTemplateResponse> createOrUpdateMvelTemplate(
      @ApiParam(value = "MVEL template name",
          required = true) @RequestParam("templateName") String templateName,
      @ApiParam(value = "MVEL template doc", required = true) @Valid @RequestBody String templateDoc) {

    String sanitizedTemplateName = LogUtil.sanitizeInput(templateName);

    MvelTemplateDto mvelTemplateDto = mvelTemplateAssembler.getMvelTemplateDto(sanitizedTemplateName, templateDoc);

    MvelTemplateResponseDto responseDto = mvelTemplateService.createMvelTemplate(mvelTemplateDto);

    MvelTemplateResponse mvelTemplateResponse = mvelTemplateAssembler.getMvelTemplateResponse(responseDto);

    return new ResponseEntity<>(mvelTemplateResponse, HttpStatus.OK);
  }
}
