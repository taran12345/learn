// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.web.rest.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response resource for Master merchant create record request.
 * 
 * @author pranavrathi
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MasterMerchantRecordCreateResponseResource {
  boolean isRecordFullyCreated;
}
