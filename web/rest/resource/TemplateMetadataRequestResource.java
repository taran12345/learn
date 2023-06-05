// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.web.rest.resource;

import com.paysafe.mastermerchant.config.Mapping;

import org.hibernate.validator.constraints.NotBlank;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TemplateMetadataResource.java.
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
public class TemplateMetadataRequestResource {

  @NotBlank(message = "tenant is a mandatory field")
  private String tenant;

  @NotBlank(message = "name is a mandatory field")
  private String name;

  private String script;
  
  private Map<String, Mapping> responseMappings;
}
