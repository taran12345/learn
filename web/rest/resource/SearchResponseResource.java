// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.web.rest.resource;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * SearchResponseResource.java.
 * 
 * @author abhineetagarwal
 *
 * 
 */
@Getter
@Setter
public class SearchResponseResource {

  private List<Map<String, Object>> records;
    
}
