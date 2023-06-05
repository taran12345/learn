// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.validation.repository;

import com.paysafe.mastermerchant.validation.repository.model.OracleResponse;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface OracleValidationRepository extends CrudRepository<OracleResponse, Long> {

  @Query(value = "select * from nbx_merchant_account_counts_vw", nativeQuery = true)
  public List<OracleResponse> getMerchantCounts();

  @Query(value = "select * from accnts_count_grp_by_status_vw", nativeQuery = true)
  public List<OracleResponse> getMerchantsGroupedByStatusCounts();

}
