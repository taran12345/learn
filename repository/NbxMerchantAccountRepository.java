// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.repository;

import com.paysafe.mastermerchant.repository.model.NbxMerchantAccountEntity;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface NbxMerchantAccountRepository extends CrudRepository<NbxMerchantAccountEntity, Long> {

  String SELECT_PREFIX =
      "SELECT ACCOUNT_ID, ACCOUNT_STATUS, ACCOUNT_STATUS_MODIFIEDDATE, COMPANY_NAME, "
          + "LEGALENTITY_CODE, LEGALENTITY_DESC, PMLE, MCC_CODE, MCC_DESCRIPTION, SALESFORCEID, "
          + "ACCOUNT_MANAGER, PAYMENT_CURRENCY, PROCESSING_CURRENCY, PROCESSING_TYPE ";

  String FIND_BY_ACCOUNT_ID_CONDITION = "FROM netbanx_merchant_accounts_vw WHERE ACCOUNT_ID = ?1";

  @Query(value = SELECT_PREFIX + FIND_BY_ACCOUNT_ID_CONDITION, nativeQuery = true)
  NbxMerchantAccountEntity findByAccountId(String accountId);

}
