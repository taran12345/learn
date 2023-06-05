// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.merchantsearch.dto;

import com.paysafe.mastermerchant.merchantsearch.enums.SearchOperator;

import org.elasticsearch.search.sort.SortOrder;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantSearchRequestDto {
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

  private Integer offset;
  private Integer limit;

  @Data
  @NotNull
  public static class RangeParamMinMax {
    @NotEmpty
    private String gte;
    @NotEmpty
    private String lte;
  }
}
