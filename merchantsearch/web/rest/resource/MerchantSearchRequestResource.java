// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.merchantsearch.web.rest.resource;

import com.paysafe.mastermerchant.merchantsearch.dto.MerchantSearchRequestDto.RangeParamMinMax;
import com.paysafe.mastermerchant.merchantsearch.enums.SearchOperator;

import org.elasticsearch.search.sort.SortOrder;
import org.hibernate.validator.constraints.Range;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.Min;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Service
public class MerchantSearchRequestResource {
  private SearchOperator operator;

  private Map<String, List<String>> filterParams;
  private Map<String, String> searchParams;
  private Map<String, List<String>> multiMatchParams;
  private List<String> existsParams;
  private Map<String, RangeParamMinMax> rangeParams;

  private List<String> responseFields;
  private List<String> distinctFields;

  private String searchAfter;
  private String sortField;
  private SortOrder sortOrder;
  
  @Min(value = 0, message = "offset should be greater than equal to 0")
  private Integer offset;
  @Range(min = 0, max = 10000, message = "Value of limit should be between 0 to 10000")
  private Integer limit;
}
