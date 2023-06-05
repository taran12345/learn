// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.service.dto;

import lombok.Data;

/**
 * Data Transfer Object of Active ThirdParties.
 *
 * @author sravyakolli
 */
@Data
public class ThirdPartiesDto {
  private Boolean equifax;
  private Boolean equifaxAcro;
  private Boolean threatMetrix;
}
