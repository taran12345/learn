// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.web.rest.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.paysafe.mastermerchant.util.DataConstants;
import com.paysafe.mastermerchant.web.rest.resource.MvelTemplateResponse;
import com.paysafe.ss.permission.client.annotations.HasOnePermission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
 * @author vijayabhaskarvanga
 */
@RestController
@RequestMapping({"mastermerchant/v1/mvel-templates"})
public class MvelTemplateController {

  @Autowired
  private AdminMvelTemplateController adminMvelTemplateController;

  /**
   * API to get template base on templateName.
   *
   * @param auth - Authorization code
   */
  @RequestMapping(produces = APPLICATION_JSON_VALUE, method = RequestMethod.GET, value = "/{templateName}")
  @HasOnePermission(DataConstants.VIEW_PERMISSION)
  public MvelTemplateResponse getByTemplateName(@RequestHeader(value = "Authorization", required = true) String auth,
      @ApiParam(value = "MVEL template name",
          required = true) @PathVariable("templateName") final String templateName) throws UnknownHostException {
    return adminMvelTemplateController.getByTemplateName(templateName);
  }

  /**
   * API to create or update MVEL template for a given template name.
   *
   * @param auth - Authorization code
   * @return MVEL template response
   */
  @RequestMapping(method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
  @HasOnePermission(DataConstants.CONFIGURE_PERMISSION)
  public ResponseEntity<MvelTemplateResponse> createOrUpdateMvelTemplate(
      @RequestHeader(value = "Authorization", required = true) String auth,
      @ApiParam(value = "MVEL template name", required = true) @RequestParam("templateName") String templateName,
      @ApiParam(value = "MVEL template doc", required = true) @Valid @RequestBody String templateDoc) {

    return adminMvelTemplateController.createOrUpdateMvelTemplate(templateName, templateDoc);
  }
}
