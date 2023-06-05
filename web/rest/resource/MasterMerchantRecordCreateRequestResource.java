// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.web.rest.resource;

import com.paysafe.mpp.commons.dto.MasterMerchantDto;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Create Request Resource for Master Merchant Record.
 *
 * @author pranavrathi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MasterMerchantRecordCreateRequestResource {

  @NotNull
  private MasterMerchantDto record;
  private String pmleId;
}
