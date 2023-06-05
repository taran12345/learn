// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.web.rest.resource;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Response resource for PMLE.
 *
 * @author narayananulaganathan
 */

@Data
public class PmleResponse {

  @JsonProperty("pmleId")
  private Long id;

  private String name;
  
  private String netbanxReference;
}

