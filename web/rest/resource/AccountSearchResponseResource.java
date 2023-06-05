// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.web.rest.resource;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto for merchant search resource.
 *
 * @author pranayhiwase
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountSearchResponseResource {

  private List<String> merchants;
  private Integer offset;
  private Integer limit;
  private Long totalSearchMatches;

}
