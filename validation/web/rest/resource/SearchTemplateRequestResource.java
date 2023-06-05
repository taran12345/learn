// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.validation.web.rest.resource;

import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class SearchTemplateRequestResource {

  @NotNull
  @Size(min = 3)
  private String templateName;

  private Map<String, Object> terms;
}
