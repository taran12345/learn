// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.service.dto;

import lombok.Data;

/**
 * Data Transfer Object of PMLE.
 *
 * @author narayananulaganathan
 */
@Data
public class PmleDto {
  private Long id;
  private String name;
  private String netbanxReference;
}
