// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.web.rest.resource;

import lombok.Data;

@Data
public class ThirdPartiesRequestResource {
  private Boolean equifax;
  private Boolean equifaxAcro;
  private Boolean threatMetrix;
}
