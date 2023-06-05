// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.web.rest.resource;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * SearchResource.java.
 * 
 * @author abhineetagarwal
 *
 * 
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequestResource {

  @NotBlank(message = "tenant is a mandatory field")
  private String tenant;

  @NotBlank(message = "template name is a mandatory field")
  private String templateName;

  @NotEmpty(message = "terms cant be null or empty")
  private Map<String, Object> terms;
}
